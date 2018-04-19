package ga.lupuss.anotherbikeapp.trackingservice.statisticsmanager

/** [Statistic] subclass that can contain [time] in milliseconds.
 * [time] might be converted to hours:minutes:seconds string.
 * @param nameId resource id to localized name
 * */
class TimeStatistic(nameId: Int, val time: Long) : Statistic(nameId) {


    override val value: String
        /** Converts time in milliseconds to string hours:minutes:seconds */
        get() {

            val hours = time / 3_600_000
            val minutes = (time - hours * 3_600_000) / 60000
            val seconds = (time - (time / 60000) * 60000) / 1000

            return "${format(hours)}:${format(minutes)}:${format(seconds)}"
        }

    /** Converts long to string. String has always min. 2 chars (filled with zero) */
    private fun format(time: Long): String {

        return if (time < 10) "0$time" else time.toString()
    }
}