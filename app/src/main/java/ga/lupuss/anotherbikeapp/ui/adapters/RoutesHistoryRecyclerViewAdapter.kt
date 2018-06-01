package ga.lupuss.anotherbikeapp.ui.adapters

import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import ga.lupuss.anotherbikeapp.R
import ga.lupuss.anotherbikeapp.models.interfaces.StringsResolver
import ga.lupuss.anotherbikeapp.models.dataclass.ShortRouteData
import ga.lupuss.anotherbikeapp.models.dataclass.Statistic
import ga.lupuss.anotherbikeapp.models.dataclass.TimeStatistic
import ga.lupuss.anotherbikeapp.models.dataclass.UnitStatistic
import ga.lupuss.anotherbikeapp.resolveTimeString

class RoutesHistoryRecyclerViewAdapter(
        private val routesDataCallback: (Int) -> ShortRouteData,
        private val sizeCallback: () -> Int,
        private val speedUnitCallback: () -> Statistic.Unit,
        private val distanceUnitCallback: () -> Statistic.Unit,
        private val stringsResolver: StringsResolver

) : RecyclerView.Adapter<RoutesHistoryRecyclerViewAdapter.ViewHolder>() {

    inner class ViewHolder(private val constraintLayout: ConstraintLayout)
        : RecyclerView.ViewHolder(constraintLayout) {

        private val speedTextView: TextView
        private val distanceTextView: TextView
        private val durationTextView: TextView
        private val whenTextView: TextView
        private val labelTextView: TextView

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

            speedTextView.text = stringsResolver.resolve(UnitStatistic(routeData.avgSpeed, speedUnitCallback.invoke()))
            distanceTextView.text = stringsResolver.resolve(UnitStatistic(routeData.distance, distanceUnitCallback.invoke()))
            durationTextView.text = stringsResolver.resolve(TimeStatistic(routeData.duration))
            whenTextView.text = resolveTimeString(context, routeData.startTime)

            labelTextView.text = routeData.name
        }
    }

    private val listeners = mutableListOf<OnItemClickListener>()

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        listeners.add(onItemClickListener)
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

        holder.itemView.setOnClickListener {

            listeners.forEach { it.onItemClick(position) }
        }
        holder.bindView(position)
    }

}