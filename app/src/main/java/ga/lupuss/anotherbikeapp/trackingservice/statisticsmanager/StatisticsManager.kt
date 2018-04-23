package ga.lupuss.anotherbikeapp.trackingservice.statisticsmanager

import android.content.Context
import android.location.Location
import android.support.v4.os.ConfigurationCompat
import com.google.android.gms.maps.model.LatLng
import ga.lupuss.anotherbikeapp.R
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

/**
 * Saves current road, measures speed, makes statistics.
 */
class StatisticsManager @Inject constructor(private val context: Context) {

    val savedRoute = mutableListOf<LatLng>()
    var lastStats: Map<Statistic.Name, Statistic>? = null

    var lastLocation: Location? = null

    private val kmh5 = 5 / Statistic.Unit.KM_H.convertParam // 5 km/h in m/s

    /** Minimum speed to record statistics */
    var minSpeedToCount = kmh5

    var speedUnit = Statistic.Unit.KM_H
    var distanceUnit = Statistic.Unit.KM

    private var distance = 0.0

    private var avgSpeed = 0.0
    private var avgCount = 0.0

    private var speed = 0.0
    private var maxSpeed = 0.0

    private var lastTime = -1L
    private var duration = 0L

    private var startTime: String = "-"

    private var status: Status = Status.LOCATION_WAIT

    private val minElementsToCount = 5
    private val tempSpeedList = mutableListOf<Double>()
    private val tempDistanceList = mutableListOf<Float>()

    var onNewStats: ((stats: Map<Statistic.Name, Statistic>) -> Unit)? = null

    val timer = Timer {

        duration = it
        onNewStats?.invoke(createStatsList())
    }

    fun onNewLocation(location: Location) {

        refreshStats(lastLocation, location)

        if ((lastLocation != null && location.distanceTo(lastLocation) != 0F)
                || lastLocation == null) {

            savedRoute.add(LatLng(location.latitude, location.longitude))
        }

        lastLocation = location
    }

    /** Takes 2 last known locations and calculate stats. */
    private fun refreshStats(before: Location?, current: Location) {

        var firstStart = false

        if (!timer.isStarted) {

            timer.start()
            timer.pause()
            firstStart = true
            startTime = recordStartTime()
        }

        val deltaTime = measureDeltaTime().toDouble()

        before ?: return

        val deltaDist = before.distanceTo(current)
        val mSpeed = deltaDist / (deltaTime / 1000)

        if (mSpeed.isInfinite()) {

            Timber.d("Infinite speed!")

        } else {

            var anyChanges = firstStart // if it's first stats refresh, onStatsUpdate should be called

            tempSpeedList.add(mSpeed)
            tempDistanceList.add(deltaDist)

            if (tempSpeedList.size >= minElementsToCount) {

                removeSpeedNoise()
                speed = tempSpeedList.average()

                anyChanges = true

                if (speed > minSpeedToCount) {

                    avgSpeed = measureAvgSpeed()
                    distance += tempDistanceList.sum()
                    timer.unpause()
                    status = Status.RUNNING

                } else {

                    timer.pause()
                    status = Status.PAUSE
                }

                if (speed > maxSpeed) {

                    maxSpeed = speed
                }

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
        distance += tempSpeedList.sum()
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

    private fun newStats() {
        val stats = createStatsList()
        lastStats = stats
        onNewStats?.invoke(stats)
    }

    /** Removes the highest and the lowest value from tempSpeedList**/
    private fun removeSpeedNoise() {

        tempSpeedList.sort()
        tempSpeedList.removeAt(0)
        tempSpeedList.removeAt(tempSpeedList.size - 1)
    }

    private fun measureDeltaTime(): Long {

        return if (lastTime == -1L) {

            lastTime = System.currentTimeMillis()

            0
        } else {

            val res = System.currentTimeMillis() - lastTime
            lastTime = System.currentTimeMillis()

            res
        }

    }

    private fun measureAvgSpeed(): Double {

        avgCount++

        return (avgSpeed * (avgCount - 1) + speed) / avgCount
    }

    private fun recordStartTime(): String {

        val calendar = Calendar.getInstance()
        val simpleDateFormat = SimpleDateFormat("HH:mm dd-MM-yyyy",
                ConfigurationCompat.getLocales(context.resources.configuration)[0])

        return simpleDateFormat.format(calendar.time)
    }

    private fun createStatsList(): Map<Statistic.Name, Statistic> {

        return linkedMapOf(
                Statistic.Name.STATUS to StringStatistic(R.string.status, status.descriptionId),
                Statistic.Name.DURATION to TimeStatistic(R.string.duration, duration),
                Statistic.Name.SPEED to UnitStatistic(R.string.speed, speed, speedUnit),
                Statistic.Name.DISTANCE to UnitStatistic(R.string.distance, distance, distanceUnit),
                Statistic.Name.AVG_SPEED to UnitStatistic(R.string.avg_speed, avgSpeed, speedUnit),
                Statistic.Name.MAX_SPEED to UnitStatistic(R.string.max_speed, maxSpeed, speedUnit),
                Statistic.Name.START_TIME to StringStatistic(R.string.start_time, startTime)
        )
    }
}
