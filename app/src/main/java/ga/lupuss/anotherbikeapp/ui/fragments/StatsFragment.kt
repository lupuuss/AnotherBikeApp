package ga.lupuss.anotherbikeapp.ui.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView

import ga.lupuss.anotherbikeapp.R
import ga.lupuss.anotherbikeapp.base.BaseActivity
import ga.lupuss.anotherbikeapp.models.interfaces.StringsResolver
import ga.lupuss.anotherbikeapp.models.pojo.Statistic
import ga.lupuss.anotherbikeapp.ui.extensions.ViewExtensions

/** Contains information about tracking. */
class StatsFragment : Fragment() {

    private lateinit var layout: LinearLayout
    private lateinit var stringsResolver: StringsResolver

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val mainView = inflater!!.inflate(R.layout.fragment_stats, container, false)
                as ScrollView
        layout = mainView.findViewById(R.id.statsContainer)

        stringsResolver = (this.activity as BaseActivity).stringsResolver

        return mainView
    }

    fun updateStats(stats: Map<Statistic.Name, Statistic<*>>) {

        if (layout.findViewById<View>(R.id.emptyStatsText) != null) {

            layout.removeAllViews()
            initStatsLayout(stats)

        } else {

            updateStatsLayout(stats)
        }
    }

    private fun initStatsLayout(stats: Map<Statistic.Name, Statistic<*>>) {

        for ((name, stat) in stats) {

            layout.addView(
                    ViewExtensions.createTextViewStatWithTag(
                            layoutInflater,
                            view!!.findViewById(R.id.statsContainer),
                            R.layout.activity_tracking_stat,
                            name,
                            stringsResolver.resolve(name, stat)
                    )
            )
        }
    }

    private fun updateStatsLayout(stats: Map<Statistic.Name, Statistic<*>>) {

        for ((name, stat) in stats) {

            ViewExtensions.updateTextViewStatByTag(layout, name, stringsResolver.resolve(name, stat))
        }
    }
}
