package ga.lupuss.anotherbikeapp.models.trackingservice.statisticsmanager

import org.junit.Assert
import org.junit.Test

class StatisticsMathProviderTest {

    @Test
    fun measureAvgSpeed_shouldMeasureAvgSpeedCorrect() {

        val speeds = listOf(1.0, 3.0, 5.0, 8.0)

        val math = StatisticsMathProvider { 1 }

        var currentAvg = 0.0

        speeds.forEach {
            currentAvg = math.measureAverage(StatisticsMathProvider.AVG.SPEED, it)
        }

        Assert.assertEquals(speeds.average(), currentAvg, 0.0)
    }
}