package ga.lupuss.anotherbikeapp.models.dataclass.statistics

import com.nhaarman.mockito_kotlin.mock
import ga.lupuss.anotherbikeapp.models.dataclass.TimeStatistic
import org.junit.Assert.*
import org.junit.Test

class TimeStatisticTest {

    private fun hours(hours: Long) = min(60) * hours

    private fun min(min: Long) = sec(60) * min

    private fun sec(sec: Long) = 1000L * sec


    private val valMaps = mapOf(
            "03:30:10" to hours(3) + min(30) + sec(10),
            "00:20:33" to hours(0) + min(20) + sec(33),
            "02:24:33" to hours(2) + min(24) + sec(33),
            "01:59:59" to hours(1) + min(59) + sec(59)
    )

    @Test
    fun timeStatistic_shouldReturnProperFormattedString() {

        for ((expected, time) in valMaps) {

            assertEquals(expected, TimeStatistic(time).getValue(mock()))
        }
    }

}