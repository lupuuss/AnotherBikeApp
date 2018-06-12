package ga.lupuss.anotherbikeapp.models.trackingservice.statisticsmanager

import kotlin.math.max

class StatisticsMathProvider(val timeProvider: () -> Long) {

    enum class AVG {
        SPEED, ALTITUDE
    }

    private data class Entity(var avgCount: Double = 0.0, var currentAvg: Double = 0.0)

    private val averages = mapOf(
            AVG.SPEED to Entity(),
            AVG.ALTITUDE to Entity()
    )

    fun measureAverage(name: AVG, currentValue: Double): Double {

        val entity = averages[name]!!

        entity.avgCount++

        entity.currentAvg = (entity.currentAvg * (entity.avgCount - 1) + currentValue) / entity.avgCount

        return entity.currentAvg
    }

    fun measureMax(current: Double, max: Double): Double {

        return max(current, max)
    }
}