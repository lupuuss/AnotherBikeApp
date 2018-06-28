package ga.lupuss.anotherbikeapp.models.trackingservice.statisticsmanager

import android.location.Location
import com.google.android.gms.maps.model.LatLng
import ga.lupuss.anotherbikeapp.models.base.PreferencesInteractor
import ga.lupuss.anotherbikeapp.models.dataclass.*
import ga.lupuss.anotherbikeapp.timeToFormattedString
import timber.log.Timber
import java.util.*
import javax.inject.Inject

/**
 * Saves current road, measures speed, makes statistics.
 */
class StatisticsManager @Inject constructor(private val locale: Locale,
                                            val timer: Timer,
                                            private val math: StatisticsMathProvider,
                                            preferencesInteractor: PreferencesInteractor) {

    private val kmh5 = 5 / Statistic.Unit.KM_H.convertParam // 5 km/h in m/s

    init {

        timer.onTimerTick = {

            routeData.duration = it
            onNewStats?.invoke(createStatsList())
        }
    }

    /** Minimum speed to record statistics */
    private val minSpeedToCount = kmh5

    var speedUnit = preferencesInteractor.speedUnit
    var distanceUnit = preferencesInteractor.distanceUnit

    var onNewStats: ((stats: Map<Statistic.Name, Statistic<*>>) -> Unit)? = null

    var lastStats: Map<Statistic.Name, Statistic<*>>? = null
    private var lastLocation: Location? = null
    val savedRoute: List<LatLng>
        get() = routeData.points

    /** Contains values that should be saved in memory or on server. */
    val routeData = MutableExtendedRouteData(
            name = null,
            points = mutableListOf(),
            avgSpeed = 0.0,
            maxSpeed = 0.0,
            distance = 0.0,
            duration = 0L,
            startTimeStr = "-",
            startTime = 0L,
            avgAltitude = 0.0,
            maxAltitude = 0.0,
            minAltitude = 0.0
    )

    // temporary stats
    // should't be saved

    private var speed = 0.0
    private var altitude = 0.0
    var status: Status = Status.LOCATION_WAIT
        private set

    fun pushNewLocation(location: Location) {

        refreshStats(lastLocation, location)

        if (
                lastLocation != null
                && location.distanceTo(lastLocation) != 0F
                && location.accuracy < 100
                || lastLocation == null
        ) {

            if (lastLocation != null && lastLocation!!.bearing == location.bearing) {

                Timber.d("<< Same bearing! Last point is replaced! >>")
                routeData.points[routeData.points.size - 1] =
                        LatLng(location.latitude, location.longitude)

            } else {

                routeData.points.add(LatLng(location.latitude, location.longitude))
            }
        } else {

            when {
                lastLocation == null -> Timber.d("Last location is null!")
                location.accuracy > 100 -> Timber.d("Accuracy is too low! -> ${location.accuracy} m")
            }

        }

        lastLocation = location
    }

    /** Takes 2 last known locations and calculate stats. */
    private fun refreshStats(before: Location?, current: Location) {

        if (!timer.isStarted) {

            timer.start()
            timer.pause()
            routeData.startTimeStr = recordStartTime()
            newStats()
        }

        before ?: return

        val distance = before.distanceTo(current)
        this.speed = current.speed.toDouble()
        this.altitude = current.altitude

        if (altitude != 0.0){
            routeData.maxAltitude = if (routeData.maxAltitude == 0.0) altitude else math.measureMax(altitude, routeData.maxAltitude)
            routeData.minAltitude = if (routeData.minAltitude == 0.0) altitude else math.measureMin(altitude, routeData.minAltitude)
            routeData.avgAltitude = math.measureAverage(StatisticsMathProvider.AVG.ALTITUDE, altitude)
        }



        if (speed.isInfinite()) {

            Timber.w("Infinite speed!")

        } else {

            if (speed > minSpeedToCount) {

                routeData.avgSpeed = math.measureAverage(StatisticsMathProvider.AVG.SPEED, speed)
                routeData.distance += distance
                timer.unpause()
                status = Status.RUNNING

            } else {

                timer.pause()
                status = Status.PAUSE
            }

            routeData.maxSpeed = math.measureMax(speed, routeData.maxSpeed)

            newStats()
        }
    }

    fun notifyLostLocation() {

        speed = 0.0
        timer.pause()
        status = Status.LOCATION_WAIT
        newStats()
    }

    fun notifyLocationOk() {

        status = Status.RUNNING
        newStats()
    }

    fun refresh() {

        newStats()
    }

    private fun newStats() {
        val stats = createStatsList()
        lastStats = stats
        onNewStats?.invoke(stats)
    }

    private fun recordStartTime(): String {

        val calendar = Calendar.getInstance()

        routeData.startTime = math.timeProvider.invoke()

        return timeToFormattedString(locale, calendar.timeInMillis)
    }

    private fun createStatsList(): Map<Statistic.Name, Statistic<*>> {
        return linkedMapOf(
                Statistic.Name.STATUS to StatusStatistic(status),
                Statistic.Name.DURATION to TimeStatistic(routeData.duration),
                Statistic.Name.DISTANCE to UnitStatistic(routeData.distance, distanceUnit),
                Statistic.Name.SPEED to UnitStatistic(speed, speedUnit),
                Statistic.Name.ALTITUDE to UnitStatistic(altitude, Statistic.Unit.M),
                Statistic.Name.AVG_SPEED to UnitStatistic(routeData.avgSpeed, speedUnit),
                Statistic.Name.MAX_SPEED to UnitStatistic(routeData.maxSpeed, speedUnit),
                Statistic.Name.AVG_ALTITUDE to UnitStatistic(routeData.avgAltitude, Statistic.Unit.M),
                Statistic.Name.MAX_ALTITUDE to UnitStatistic(routeData.maxAltitude, Statistic.Unit.M),
                Statistic.Name.MIN_ALTITUDE to UnitStatistic(routeData.minAltitude, Statistic.Unit.M),
                Statistic.Name.START_TIME to StringStatistic(routeData.startTimeStr)
        )
    }
}
