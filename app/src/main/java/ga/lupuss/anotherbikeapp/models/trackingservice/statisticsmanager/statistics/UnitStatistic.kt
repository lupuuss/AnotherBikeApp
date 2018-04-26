package ga.lupuss.anotherbikeapp.models.trackingservice.statisticsmanager.statistics

import android.content.Context

/** [Statistic] subclass that can contain [value] as double.
 * [value] might be converted to any [unit].
 * */
class UnitStatistic(
        private val value: Double,
        private val unit: Unit

) : Statistic() {

    override fun getValue(context: Context) =
            (Math.round(value * unit.convertParam * 100.0) / 100.0).toString() + " " + context.getString(unit.suffix)
}
