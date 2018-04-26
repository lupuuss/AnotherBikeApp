package ga.lupuss.anotherbikeapp.models.trackingservice.statisticsmanager.statistics

import android.content.Context
import ga.lupuss.anotherbikeapp.R

/**
 * Base class for any statistic.
 */
abstract class Statistic {
    /** Names of possible statistics */
    enum class Name(private val nameId: Int) {

        SPEED(R.string.speed),
        AVG_SPEED(R.string.avg_speed),
        MAX_SPEED(R.string.max_speed),
        DISTANCE(R.string.distance),
        DURATION(R.string.duration),
        STATUS(R.string.status),
        START_TIME(R.string.start_time);

        fun getName(context: Context): String {
            return context.getString(nameId)
        }
    }

    /**
     * Represent units used in app.
     *
     * @param convertParam is used to convert SI units to others
     * e.g 3.6 km/h is 1 m/s so convertParam is 3.6
     * @param suffix represents unit suffix like m/s for metre per second
     */
    enum class Unit(val suffix: Int, val convertParam: Double) {
        M_S(R.string.unit_speed_ms, 1.0), // SI unit
        KM_H(R.string.unit_speed_kmh, 3.6),
        M(R.string.unit_distance_m, 1.0), // SI unit
        KM(R.string.unit_distance_km, 0.001)
    }

    /** Returns statistic value. Some [Statistic] subclasses may need [context] to return proper value. */
    abstract fun getValue(context: Context): String
}