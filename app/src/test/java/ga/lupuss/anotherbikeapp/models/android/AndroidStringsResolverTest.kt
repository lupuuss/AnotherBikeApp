package ga.lupuss.anotherbikeapp.models.android

import android.content.Context
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import ga.lupuss.anotherbikeapp.Message
import ga.lupuss.anotherbikeapp.Text
import ga.lupuss.anotherbikeapp.models.dataclass.Statistic
import ga.lupuss.anotherbikeapp.models.dataclass.StatusStatistic
import ga.lupuss.anotherbikeapp.models.dataclass.TimeStatistic
import ga.lupuss.anotherbikeapp.models.dataclass.UnitStatistic
import ga.lupuss.anotherbikeapp.models.trackingservice.statisticsmanager.Status
import org.junit.Test

import org.junit.BeforeClass

class AndroidStringsResolverTest {

    companion object {

        lateinit var context: Context
        lateinit var stringsResolver: AndroidStringsResolver

        @BeforeClass
        @JvmStatic
        fun mockContext() {

            context = mock {
                on { it.getString(any()) }.then { "Not empty string" }
            }

            stringsResolver = AndroidStringsResolver(context)
        }
    }

    @Test
    fun resolveForResourcesBaseStrings_shouldAlwaysReturnNotEmptyString() {

        for (message in Message.values()) {

            assert(stringsResolver.resolve(message) != "")
        }

        for (text in Text.values()) {

            assert(stringsResolver.resolve(text) != "")
        }

        for (unitName in Statistic.Name.values()) {

            assert(stringsResolver.resolve(unitName) != "")
        }

        for (unit in Statistic.Unit.values()) {

            assert(stringsResolver.resolve(unit) != "")
        }

        for (status in Status.values()) {

            assert(stringsResolver.resolve(StatusStatistic(status)) != "")
        }
    }

    @Test
    fun resolveForTimeUnit_shouldReturnProperTimeString() {

        fun sec(sec: Long) = 1000L * sec
        fun min(min: Long) = sec(60) * min
        fun hours(hours: Long) = min(60) * hours

        val valMaps = mapOf(
                "03:30:10" to hours(3) + min(30) + sec(10),
                "00:20:33" to hours(0) + min(20) + sec(33),
                "02:24:33" to hours(2) + min(24) + sec(33),
                "01:59:59" to hours(1) + min(59) + sec(59)
        )

        for ((str, long) in valMaps) {

            assert(stringsResolver.resolve(TimeStatistic(long)) == str)
        }

    }

    @Test
    fun resolveForStatNameAndStat_shouldAlwaysReturnProperlyFormattedString() {

        assert(
                stringsResolver.resolve(
                    Statistic.Name.SPEED,
                    UnitStatistic(3.0, Statistic.Unit.KM_H)
                ).matches(Regex(".*: .*"))
        )
    }

    @Test
    fun resolveForUnitStat_shouldAlwaysReturnProperlyFormattedString() {

        assert(
                stringsResolver.resolve(UnitStatistic(3.0, Statistic.Unit.KM_H))
                        .matches(Regex("(\\d*\\.)?\\d+ .+"))
        )
    }
}