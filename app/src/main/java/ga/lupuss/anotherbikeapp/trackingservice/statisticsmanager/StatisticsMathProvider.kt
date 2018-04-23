package ga.lupuss.anotherbikeapp.trackingservice.statisticsmanager

import kotlin.math.max

class StatisticsMathProvider(private val timeProvider: () -> Long) {

    private var lastTime = -1L
    private var avgCount = 0.0

    /** Removes the highest and the lowest value from tempSpeedList**/
    fun removeSpeedNoise(tempSpeedList: MutableList<Double>) {

        tempSpeedList.sort()
        tempSpeedList.removeAt(0)
        tempSpeedList.removeAt(tempSpeedList.size - 1)
    }

    fun measureDeltaTime(): Long {

        return if (lastTime == -1L) {

            lastTime = timeProvider.invoke()

            0
        } else {

            val res = timeProvider.invoke() - lastTime
            lastTime = timeProvider.invoke()

            res
        }

    }

    fun measureAvgSpeed(currentAvg: Double, currentSpeed: Double): Double {

        avgCount++

        return (currentAvg * (avgCount - 1) + currentSpeed) / avgCount
    }

    fun measureMaxSpeed(currentSpeed: Double, maxSpeed: Double): Double {

        return max(currentSpeed, maxSpeed)
    }
}