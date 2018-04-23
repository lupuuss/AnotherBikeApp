package ga.lupuss.anotherbikeapp.trackingservice.statisticsmanager.statistics

import android.content.Context
import ga.lupuss.anotherbikeapp.trackingservice.statisticsmanager.statistics.Statistic

/** [Statistic] subclass that can contain [value] as double.
 * [value] might be converted to any [unit].
 * @param nameId resource id to localized name
 * */
class UnitStatistic(
        nameId: Int,
        private val value: Double,
        private val unit: Unit

) : Statistic(nameId) {

    override fun getValue(context: Context) =
            (Math.round(value * unit.convertParam * 100.0) / 100.0).toString() + " " + context.getString(unit.suffix)
}
