package ga.lupuss.anotherbikeapp.ui.extensions

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import ga.lupuss.anotherbikeapp.trackingservice.statisticsmanager.statistics.Statistic

@SuppressLint("SetTextI18n")

fun TextView?.setText(stat: Statistic) {
    this?.context ?: return
    text = "${stat.getName(context)}: ${stat.getValue(context)}"
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

            (statTextView as TextView).setText(stat)
        }

        /**
         * Create TextView, which contains statistic. TextView receives tag - name of statistic (Statistic.Name).
         * @param rootView ViewGroup that will contain statistic
         * @param statLayoutId Id of layout, which contains TextView that represents statistic
         * @param name Name of statistic. It's a tag for a TextView
         */
        fun createStatLineWithNameTag(layoutInflater: LayoutInflater, rootView: ViewGroup,
                                      statLayoutId: Int, name: Statistic.Name,
                                      stat: Statistic): TextView {

            val statTextView = layoutInflater
                    .inflate(statLayoutId, rootView, false)

            statTextView.tag = (name)
            (statTextView as TextView).setText(stat)

            return statTextView
        }

    }
}