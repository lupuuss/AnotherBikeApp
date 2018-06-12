package ga.lupuss.anotherbikeapp.models.trackingservice.statisticsmanager

import kotlin.math.max

class StatisticsMathProvider(val timeProvider: () -> Long) {

    private var avgCount = 0.0
    private var currentAvg = 0.0

    fun measureAvgSpeed(currentSpeed: Double): Double {

        avgCount++

        currentAvg = (currentAvg * (avgCount - 1) + currentSpeed) / avgCount

        return currentAvg
    }

    fun measureMaxSpeed(currentSpeed: Double, maxSpeed: Double): Double {

        return max(currentSpeed, maxSpeed)
    }
}