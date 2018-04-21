package ga.lupuss.anotherbikeapp.trackingservice.statisticsmanager

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import ga.lupuss.anotherbikeapp.R

/**
 * Base class for any statistic.
 */
abstract class Statistic(
        /** resource id with localized statistic name */
        private val nameId: Int
) {
    /** Names of possible statistics */
    enum class Name {
        SPEED, AVG_SPEED, MAX_SPEED, DISTANCE, DURATION, STATUS, START_TIME
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

    /** Statistics names are defined by resources so [context] is necessary. */
    fun getName(context: Context): String {

        return context.getString(nameId)
    }

    /** Returns statistic value. Some [Statistic] subclasses may need [context] to return proper value. */
    abstract fun getValue(context: Context): String

    companion object {

        /** Set formatted statistic as text in passed TextView */
        @SuppressLint("SetTextI18n")
        fun setStatForTextView(context: Context, textView: TextView, stat: Statistic) {

            textView.text =
                    "${stat.getName(context)}: ${stat.getValue(context)}"
        }

        /**
         *  Set formatted statistic as text in TextView with tag (Statistic.Name)
         *  @param layout Layout that contains statistic (TextView)
         *  @param tag Tag that was assigned to TextView by createStatsLineWithNameTag
         *
         */
        fun updateStatLineByTag(context: Context, layout: ViewGroup,
                                tag: Name, stat: Statistic) {

            val statTextView = layout.findViewWithTag<TextView>(tag)

            setStatForTextView(context, statTextView as TextView, stat)
        }

        /**
         * Create TextView, which contains statistic. TextView receives tag - name of statistic (Statistic.Name).
         * @param rootView ViewGroup that will contain statistic
         * @param statLayoutId Id of layout, which contains TextView that represents statistic
         * @param name Name of statistic. It's a tag for a TextView
         */
        fun createStatLineWithNameTag(layoutInflater: LayoutInflater, rootView: ViewGroup,
                                      statLayoutId: Int, name: Name,
                                      stat: Statistic): TextView {

            val statTextView = layoutInflater
                    .inflate(statLayoutId, rootView, false)

            statTextView.tag = (name)
            setStatForTextView(layoutInflater.context, statTextView as TextView, stat)

            return statTextView
        }

    }
}