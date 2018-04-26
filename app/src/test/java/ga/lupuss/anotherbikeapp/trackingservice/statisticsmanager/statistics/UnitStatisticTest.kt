package ga.lupuss.anotherbikeapp.trackingservice.statisticsmanager.statistics

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import ga.lupuss.anotherbikeapp.models.trackingservice.statisticsmanager.statistics.Statistic
import ga.lupuss.anotherbikeapp.models.trackingservice.statisticsmanager.statistics.UnitStatistic
import org.junit.Assert.*
import org.junit.Test

class UnitStatisticTest {

    @Test
    fun unitStatisticValue_shouldHas2DecimalPlaces() {

        assertEquals(
                "23.23 m/s",
                UnitStatistic(23.23312, Statistic.Unit.M_S)
                        .getValue(
                                mock {
                                    on { getString(any()) }.thenReturn("m/s")
                                }
                        )
        )
    }

    @Test
    fun unitStatisticValue_shouldBeConvertedToProperUnit() {

        assertEquals(
                "3.6 km/h",
                UnitStatistic(1.0, Statistic.Unit.KM_H)
                        .getValue(
                                mock {
                                    on { getString(any()) }.thenReturn("km/h")
                                }
                        )
        )

    }
}