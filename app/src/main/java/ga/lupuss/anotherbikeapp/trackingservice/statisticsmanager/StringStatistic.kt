package ga.lupuss.anotherbikeapp.trackingservice.statisticsmanager

import android.content.Context

/**
 * [Statistic] basic implementation.
 */
class StringStatistic(nameId: Int, private val str: String) : Statistic(nameId) {

    private var resId: Int? = null

    constructor(nameId: Int, resId: Int) : this(nameId, "") {
        this.resId = resId
    }

    override fun getValue(context: Context): String =
            if (resId == null) str else context.getString(resId!!)

}