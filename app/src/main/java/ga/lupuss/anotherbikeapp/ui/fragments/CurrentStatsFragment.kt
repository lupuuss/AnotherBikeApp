package ga.lupuss.anotherbikeapp.ui.fragments

import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.ScrollView

import ga.lupuss.anotherbikeapp.R
import ga.lupuss.anotherbikeapp.base.ThemedActivity
import ga.lupuss.anotherbikeapp.models.base.ResourceResolver
import ga.lupuss.anotherbikeapp.models.dataclass.Statistic
import ga.lupuss.anotherbikeapp.AnotherBikeApp
import ga.lupuss.anotherbikeapp.models.trackingservice.statisticsmanager.Status
import ga.lupuss.anotherbikeapp.ui.extensions.ViewExtensions
import ga.lupuss.anotherbikeapp.ui.extensions.isVisible
import kotlinx.android.synthetic.main.fragment_current_stats.*


/** Contains information about tracking. */
class CurrentStatsFragment : Fragment() {

    private var layout: LinearLayout? = null
    private lateinit var resourceResolver: ResourceResolver

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val mainView = inflater.inflate(R.layout.fragment_current_stats, container, false)
        layout = mainView.findViewById(R.id.statsContainer)

        resourceResolver = (this.activity as ThemedActivity).resourceResolver

        return mainView
    }

    fun updateStats(statsArg: Map<Statistic.Name, Statistic<*>>) {

        if (statsArg.contains(Statistic.Name.STATUS)) {

            statusIcon.setImageResource(resolveStatusIcon(statsArg[Statistic.Name.STATUS]!!.value as Status))
        } else {

            statusIcon.isVisible = false
        }

        durationText.text = resourceResolver.resolve(statsArg[Statistic.Name.DURATION]!!)

        @Suppress("NAME_SHADOWING")
        val stats = statsArg.toMutableMap()

        stats.remove(Statistic.Name.DURATION)
        stats.remove(Statistic.Name.STATUS)
        stats.remove(Statistic.Name.START_TIME)

        if (leftContainer.childCount == 0 && rightContainer.childCount == 0) {

            initStats(stats)
        } else {
            updateStatsHelper(stats)
        }

    }

    private fun initStats(stats: Map<Statistic.Name, Statistic<*>>) {

        var i = 0

        stats.forEach { name, statistic ->

            val linearLayout: LinearLayout
            val textLayout: Int

            if (i % 2 == 0) {
                linearLayout = leftContainer
                textLayout = R.layout.left_stat
            } else {
                linearLayout = rightContainer
                textLayout = R.layout.right_stat
            }

            linearLayout.addView(
                    ViewExtensions.createStatWithTag(
                            layoutInflater, linearLayout, textLayout, name,
                            resourceResolver.resolve(name), resourceResolver.resolve(statistic)
                    )
            )

            i++
        }
    }

    private fun updateStatsHelper(stats: Map<Statistic.Name, Statistic<*>>) {

        var i = 0

        stats.forEach { name, statistic ->

            val linearLayout: LinearLayout = if (i % 2 == 0) {

                leftContainer
            } else {
                rightContainer
            }

            ViewExtensions.updateStatByTag(
                    linearLayout,
                    name,
                    resourceResolver.resolve(name),
                    resourceResolver.resolve(statistic)
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
        layout = null
        val refWatcher = AnotherBikeApp.getRefWatcher(this.activity!!.applicationContext)
        refWatcher.watch(this)
    }
}
