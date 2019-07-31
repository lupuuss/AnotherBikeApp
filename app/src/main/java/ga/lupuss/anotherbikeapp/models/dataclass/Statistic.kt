package ga.lupuss.anotherbikeapp.models.dataclass

import ga.lupuss.anotherbikeapp.AppUnit
import ga.lupuss.anotherbikeapp.models.trackingservice.statisticsmanager.Status
import java.util.*

/**
 * Base class for any statistic.
 */
sealed class Statistic<T> {

    /** Names of possible statistics */
    enum class Name {

        SPEED,
        AVG_SPEED,
        MAX_SPEED,
        DISTANCE,
        DURATION,
        STATUS,
        START_TIME,
        ALTITUDE,
        AVG_ALTITUDE,
        MAX_ALTITUDE,
        MIN_ALTITUDE
    }

    abstract val value: T

    override fun equals(other: Any?): Boolean {

        return if (other !is Statistic<*> || other.javaClass != javaClass) {

            false

        } else {

            this.value == other.value
        }
    }

    override fun hashCode(): Int = Objects.hash(value)
}

class UnitStatistic(override val value: Double, val unit: AppUnit) : Statistic<Double>()

class TimeStatistic(override val value: Long) : Statistic<Long>()

class StringStatistic(override val value: String) : Statistic<String>()

class StatusStatistic(override val value: Status) : Statistic<Status>()