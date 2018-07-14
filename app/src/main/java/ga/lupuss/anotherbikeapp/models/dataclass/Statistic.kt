package ga.lupuss.anotherbikeapp.models.dataclass

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

    /**
     * Represent units used in app.
     *
     * @property convertParam is used to convert SI units to others
     * e.g 3.6 km/h is 1 m/s so convertParam is 3.6
     */
    interface Unit {

        val convertParam: Double

        enum class Distance(override val convertParam: Double): Unit {

            M( 1.0), // SI unit
            KM(0.001),
            MI(0.000621371192)
        }

        enum class Speed(override val convertParam: Double): Unit {

            M_S(1.0), // SI unit
            KM_H( 3.6),
            MPH(2.23693629)
        }
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

/** [Statistic] subclass that can contain [value] as double.
 * [value] might be converted to any [unit].
 * */

class UnitStatistic(override val value: Double, val unit: Unit) : Statistic<Double>()
class TimeStatistic(override val value: Long) : Statistic<Long>()
class StringStatistic(override val value: String) : Statistic<String>()
class StatusStatistic(override val value: Status) : Statistic<Status>()