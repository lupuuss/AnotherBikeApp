package ga.lupuss.anotherbikeapp.ui.fragments

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.widget.NestedScrollView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout

import ga.lupuss.anotherbikeapp.R
import ga.lupuss.anotherbikeapp.base.ThemedActivity
import ga.lupuss.anotherbikeapp.models.base.ResourceResolver
import ga.lupuss.anotherbikeapp.models.dataclass.Statistic
import ga.lupuss.anotherbikeapp.AnotherBikeApp
import ga.lupuss.anotherbikeapp.models.trackingservice.statisticsmanager.Status
import ga.lupuss.anotherbikeapp.ui.extensions.ViewExtensions
import ga.lupuss.anotherbikeapp.ui.extensions.isGone
import ga.lupuss.anotherbikeapp.ui.extensions.isVisible
import kotlinx.android.synthetic.main.fragment_stats.*


/** Contains information about tracking. */
class StatsFragment : Fragment() {

    private var isScrollingEnabled: Boolean = true
    private lateinit var resourceResolver: ResourceResolver
    private var isLandscape = false

    enum class Mode {
        CURRENT_STATS, SUMMARY_STATS
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val mainView = inflater.inflate(R.layout.fragment_stats, container, false)

        resourceResolver = (this.activity as ThemedActivity).resourceResolver

        isLandscape = requireContext().resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

        (mainView as NestedScrollView).setOnTouchListener { _, _ ->
            !isScrollingEnabled
        }

        return mainView
    }

    fun updateStats(statsArg: Map<Statistic.Name, Statistic<*>>, mode: Mode) {

        @Suppress("NAME_SHADOWING")
        val stats = statsArg.toMutableMap()

        if (mode == Mode.CURRENT_STATS) {

            statusIcon.setImageResource(resolveStatusIcon(statsArg.getValue(Statistic.Name.STATUS).value as Status))

            durationText.text = resourceResolver.resolve(statsArg.getValue(Statistic.Name.DURATION))
            stats.remove(Statistic.Name.DURATION)
        } else {

            durationText.isGone = true
            statusIcon.isVisible = false
        }



        stats.remove(Statistic.Name.STATUS)
        stats.remove(Statistic.Name.START_TIME)

        if (leftContainer.childCount == 0) {

            initStats(stats)

        } else {
            updateStatsHelper(stats)
        }

    }

    @Suppress("PLUGIN_WARNING")
    private fun initStats(stats: Map<Statistic.Name, Statistic<*>>) {

        var i = 0

        stats.entries.forEach { (name, statistic) ->

            val linearLayout: LinearLayout
            val textLayout: Int

            if (i % 2 == 0 || isLandscape) {

                linearLayout = leftContainer
                textLayout = R.layout.fragment_stats_stat_left
            } else {

                linearLayout = rightContainer
                textLayout = R.layout.fragment_stats_stat_right
            }

            linearLayout.addView(
                    ViewExtensions.createStatWithTag(
                            layoutInflater, linearLayout, textLayout, name,
                            resourceResolver.resolve(name), resourceResolver.resolve(statistic)
                    )
            )

            i++
        }

        if (i % 2 == 1 && !isLandscape) {

            // fills empty place in right container
            rightContainer.addView(layoutInflater.inflate(R.layout.fragment_stats_stat_right, rightContainer, false))
        }
    }

    @Suppress("PLUGIN_WARNING")
    private fun updateStatsHelper(stats: Map<Statistic.Name, Statistic<*>>) {

        var i = 0

        stats.entries.forEach { (name, stat) ->

            val linearLayout: LinearLayout = if (i % 2 == 0 || isLandscape) {

                leftContainer
            } else {

                rightContainer
            }

            ViewExtensions.updateStatByTag(
                    linearLayout,
                    name,
                    resourceResolver.resolve(name),
                    resourceResolver.resolve(stat)
            )

            i++
        }
    }

    private fun resolveStatusIcon(status: Status): Int {
        return when(status) {

            Status.LOCATION_WAIT -> R.drawable.ic_location_off_24dp
            Status.PAUSE -> R.drawable.ic_pause_24dp
            Status.RUNNING -> R.drawable.ic_my_location_24dp
        }
    }

    override fun onDestroy() {

        super.onDestroy()
        val refWatcher = AnotherBikeApp.getRefWatcher(this.activity!!.applicationContext)
        refWatcher.watch(this)
    }
}
