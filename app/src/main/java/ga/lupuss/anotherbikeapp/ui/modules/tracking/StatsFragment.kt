package ga.lupuss.anotherbikeapp.ui.modules.tracking

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView

import ga.lupuss.anotherbikeapp.R
import ga.lupuss.anotherbikeapp.trackingservice.statisticsmanager.Statistic

/** Contains information about tracking. */
class StatsFragment : Fragment() {

    companion object {

        fun newInstance(): StatsFragment {

            return StatsFragment()
        }
    }

    private lateinit var layout: LinearLayout

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val mainView = inflater!!.inflate(R.layout.fragment_stats, container, false)
                as ScrollView
        layout = mainView.findViewById(R.id.statsContainer)

        return mainView
    }

    fun updateStats(stats: Map<Statistic.Name, Statistic>) {

        if (layout.findViewById<View>(R.id.emptyStatsText) != null) {

            layout.removeAllViews()
            initStatsLayout(stats)

        } else {

            updateStatsLayout(stats)
        }
    }

    private fun initStatsLayout(stats: Map<Statistic.Name, Statistic>) {

        for ((name, stat) in stats) {

            layout.addView(
                    Statistic.createStatLineWithNameTag(
                            layoutInflater,
                            view!!.findViewById(R.id.statsContainer),
                            R.layout.activity_tracking_stat,
                            name,
                            stat
                    )
            )
        }
    }

    private fun updateStatsLayout(stats: Map<Statistic.Name, Statistic>) {

        for ((name, stat) in stats) {

            Statistic.updateStatLineByTag(this.context, layout, name, stat)
        }
    }
}
