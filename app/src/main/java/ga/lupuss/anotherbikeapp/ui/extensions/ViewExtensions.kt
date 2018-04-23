package ga.lupuss.anotherbikeapp.ui.extensions

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import ga.lupuss.anotherbikeapp.trackingservice.statisticsmanager.statistics.Statistic

@SuppressLint("SetTextI18n")

fun TextView?.setText(statName: Statistic.Name, stat: Statistic) {
    this?.context ?: return
    text = "${statName.getName(context)}: ${stat.getValue(context)}"
}

class ViewExtensions {

    /** Contains methods to create statistic's views. */
    companion object {

        /**
         *  Set formatted statistic as text in TextView with tag (Statistic.Name)
         *  @param layout Layout that contains statistic (TextView)
         *  @param tag Tag that was assigned to TextView by createStatsLineWithNameTag
         *
         */
        fun updateStatLineByTag(layout: ViewGroup,
                                tag: Statistic.Name, stat: Statistic) {

            val statTextView = layout.findViewWithTag<TextView>(tag)

            (statTextView as TextView).setText(tag, stat)
        }

        /**
         * Create TextView, which contains statistic. TextView receives tag - name of statistic (Statistic.Name).
         * @param rootView ViewGroup that will contain statistic
         * @param statLayoutId Id of layout, which contains TextView that represents statistic
         * @param statName Name of statistic. It's a tag for a TextView
         */
        fun createStatLineWithNameTag(layoutInflater: LayoutInflater, rootView: ViewGroup,
                                      statLayoutId: Int, statName: Statistic.Name,
                                      stat: Statistic): TextView {

            val statTextView = layoutInflater
                    .inflate(statLayoutId, rootView, false)

            statTextView.tag = (statName)
            (statTextView as TextView).setText(statName, stat)

            return statTextView
        }

    }
}