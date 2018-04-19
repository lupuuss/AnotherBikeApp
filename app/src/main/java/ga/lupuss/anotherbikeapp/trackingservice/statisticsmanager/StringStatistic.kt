package ga.lupuss.anotherbikeapp.trackingservice.statisticsmanager

/**
 * [Statistic] basic implementation.
 */
class StringStatistic(nameId: Int, private val status: String) : Statistic(nameId) {


    override val value: String
        get() = status

}