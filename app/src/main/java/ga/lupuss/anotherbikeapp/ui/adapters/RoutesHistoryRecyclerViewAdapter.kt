package ga.lupuss.anotherbikeapp.ui.adapters

import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import ga.lupuss.anotherbikeapp.R
import ga.lupuss.anotherbikeapp.models.pojo.ShortRouteData
import ga.lupuss.anotherbikeapp.models.trackingservice.statisticsmanager.statistics.Statistic
import ga.lupuss.anotherbikeapp.models.trackingservice.statisticsmanager.statistics.TimeStatistic
import ga.lupuss.anotherbikeapp.models.trackingservice.statisticsmanager.statistics.UnitStatistic
import ga.lupuss.anotherbikeapp.resolveTimeString

@Suppress("MemberVisibilityCanBePrivate")
class RoutesHistoryRecyclerViewAdapter(
        private val routesDataCallback: (Int) -> ShortRouteData,
        private val sizeCallback: () -> Int,
        private val speedUnitCallback: () -> Statistic.Unit,
        private val distanceUnitCallback: () -> Statistic.Unit

) : RecyclerView.Adapter<RoutesHistoryRecyclerViewAdapter.ViewHolder>() {

    inner class ViewHolder(val constraintLayout: ConstraintLayout)
        : RecyclerView.ViewHolder(constraintLayout) {

        val speedTextView: TextView
        val distanceTextView: TextView
        val durationTextView: TextView
        val whenTextView: TextView
        val labelTextView: TextView

        init {

            fun findTextView(id: Int) = constraintLayout.findViewById<TextView>(id)


            speedTextView = findTextView(R.id.speedStat)
            durationTextView = findTextView(R.id.durationStat)
            distanceTextView = findTextView(R.id.distanceStat)
            whenTextView = findTextView(R.id.`when`)
            labelTextView = findTextView(R.id.label)
        }

        fun bindView(n: Int) {

            fillStats(routesDataCallback.invoke(n))
            constraintLayout.tag = this
        }

        private fun fillStats(routeData: ShortRouteData) {

            val context = constraintLayout.context.applicationContext

            speedTextView.text = UnitStatistic(routeData.avgSpeed, speedUnitCallback.invoke()).getValue(context)
            distanceTextView.text = UnitStatistic(routeData.distance, distanceUnitCallback.invoke()).getValue(context)
            durationTextView.text = TimeStatistic(routeData.duration).getValue(context)
            whenTextView.text = resolveTimeString(context, routeData.startTime)

            labelTextView.text = routeData.name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {

        val constraintLayout = LayoutInflater
                .from(parent?.context)
                .inflate(R.layout.saved_route_layout, parent, false)
                as ConstraintLayout

        return ViewHolder(constraintLayout)
    }

    override fun getItemCount(): Int = sizeCallback.invoke()

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.bindView(position)
    }

}