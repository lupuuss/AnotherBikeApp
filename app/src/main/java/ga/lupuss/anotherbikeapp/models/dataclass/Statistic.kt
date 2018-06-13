package ga.lupuss.anotherbikeapp.models.dataclass

import ga.lupuss.anotherbikeapp.models.trackingservice.statisticsmanager.Status

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
     * @param convertParam is used to convert SI units to others
     * e.g 3.6 km/h is 1 m/s so convertParam is 3.6
     */
    enum class Unit(val convertParam: Double) {
        M_S(1.0), // SI unit
        KM_H( 3.6),
        MPH(2.23693629),
        M( 1.0), // SI unit
        KM(0.001),
        MI(0.000621371192)
    }

    abstract val value: T
}

/** [Statistic] subclass that can contain [value] as double.
 * [value] might be converted to any [unit].
 * */

class UnitStatistic(override val value: Double, val unit: Unit) : Statistic<Double>()
class TimeStatistic(override val value: Long) : Statistic<Long>()
class StringStatistic(override val value: String) : Statistic<String>()
class StatusStatistic(override val value: Status) : Statistic<Status>()