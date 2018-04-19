package ga.lupuss.anotherbikeapp.trackingservice.statisticsmanager

/** [Statistic] subclass that can contain [_value] as double.
 * [_value] might be converted to any [unit].
 * @param nameId resource id to localized name
 * */
class UnitStatistic(
        nameId: Int,
        private val _value: Double,
        private val unit: Unit

) : Statistic(nameId) {

    override val value
        /** Converts value in double, to string.
         * If necessary converts to another unit.
         * E.G. 54.233 to "54.2 m/s". */
        get() = (Math.round(_value * unit.convertParam * 100.0) / 100.0).toString() + " " + unit.suffix
}
