package ga.lupuss.anotherbikeapp.models.trackingservice.statisticsmanager.statistics

import android.content.Context
import ga.lupuss.anotherbikeapp.models.trackingservice.statisticsmanager.statistics.Statistic

/**
 * [Statistic] basic implementation.
 */
class StringStatistic(private val str: String) : Statistic() {

    private var resId: Int? = null

    constructor(resId: Int) : this("") {
        this.resId = resId
    }

    override fun getValue(context: Context): String =
            if (resId == null) str else context.getString(resId!!)

}