package ga.lupuss.anotherbikeapp.models.trackingservice.statisticsmanager

import junit.framework.TestCase.assertEquals
import org.junit.Test

class StatisticsMathProviderTest {

    @Test
    fun measureAvgSpeed_shouldMeasureAvgSpeedCorrect() {

        val speeds = listOf(1.0, 3.0, 5.0, 8.0)

        val math = StatisticsMathProvider({ 1 })

        var currentAvg = 0.0

        speeds.forEach {
            currentAvg = math.measureAvgSpeed(it)
        }

        assertEquals(speeds.average(), currentAvg)

    }
}