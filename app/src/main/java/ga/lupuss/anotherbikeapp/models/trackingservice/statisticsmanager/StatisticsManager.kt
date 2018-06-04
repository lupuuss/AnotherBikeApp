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
    var lastLocation: Location? = null
    val savedRoute: List<LatLng>
        get() = routeData.points

    /** Contains values that should be saved in memory or on server. */
    val routeData = ExtendedRouteData(
            name = null,
            points = mutableListOf(),
            avgSpeed = 0.0,
            maxSpeed = 0.0,
            distance = 0.0,
            duration = 0L,
            startTimeStr = "-",
            startTime = 0L
    )

    // temporary stats
    // should't be saved

    private var speed = 0.0
    var status: Status = Status.LOCATION_WAIT
        private set

    val minElementsToCount = 5
    private val tempSpeedList = mutableListOf<Double>()
    private val tempDistanceList = mutableListOf<Float>()

    fun pushNewLocation(location: Location) {

        refreshStats(lastLocation, location)

        if ((lastLocation != null
                        && location.distanceTo(lastLocation) != 0F
                        && location.accuracy < 100)
                || lastLocation == null) {

            if (lastLocation != null && lastLocation!!.bearing == location.bearing) {

                routeData.points[routeData.points.size - 1] =
                        LatLng(location.latitude, location.longitude)

            } else {

                routeData.points.add(LatLng(location.latitude, location.longitude))
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

        val deltaTime = math.measureDeltaTime().toDouble()

        before ?: return

        val deltaDist = before.distanceTo(current)
        val mSpeed = deltaDist / (deltaTime / 1000)

        if (mSpeed.isInfinite()) {

            Timber.d("Infinite speed!")

        } else {

            var anyChanges = false

            tempSpeedList.add(mSpeed)
            tempDistanceList.add(deltaDist)

            if (tempSpeedList.size >= minElementsToCount) {

                math.removeSpeedNoise(tempSpeedList)
                speed = tempSpeedList.average()

                anyChanges = true

                if (speed > minSpeedToCount) {

                    routeData.avgSpeed = math.measureAvgSpeed(speed)
                    routeData.distance += tempDistanceList.sum()
                    timer.unpause()
                    status = Status.RUNNING

                } else {

                    timer.pause()
                    status = Status.PAUSE
                }

                routeData.maxSpeed = math.measureMaxSpeed(speed, routeData.maxSpeed)

                tempSpeedList.clear()
                tempDistanceList.clear()
            }

            if (anyChanges) {

                newStats()
            }
        }
    }

    fun notifyLostLocation() {

        tempSpeedList.clear()
        routeData.distance += tempSpeedList.sum()
        tempDistanceList.clear()
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
                Statistic.Name.SPEED to UnitStatistic(speed, speedUnit),
                Statistic.Name.DISTANCE to UnitStatistic(routeData.distance, distanceUnit),
                Statistic.Name.AVG_SPEED to UnitStatistic(routeData.avgSpeed, speedUnit),
                Statistic.Name.MAX_SPEED to UnitStatistic(routeData.maxSpeed, speedUnit),
                Statistic.Name.START_TIME to StringStatistic(routeData.startTimeStr)
        )
    }
}
