package ga.lupuss.anotherbikeapp.trackingservice.statisticsmanager

import ga.lupuss.anotherbikeapp.models.trackingservice.statisticsmanager.StatisticsMathProvider
import junit.framework.Assert.assertEquals
import org.junit.Test

class StatisticsMathProviderTest {

    @Test
    fun removeSpeedNoise_removesHighestAndLowestValueFromList() {

        val start = mutableListOf(1.0, 2.0, 3.0, 4.0, 5.0)
        start.shuffle()

        StatisticsMathProvider({ 0 }).removeSpeedNoise(start)

        start.sort()
        assertEquals(mutableListOf(2.0, 3.0, 4.0), start)
    }

    @Test
    fun firstDelta_shouldBeZero() {
        assertEquals(0, StatisticsMathProvider({ 1 }).measureDeltaTime())
    }

    @Test
    fun measureDelta_shouldMeasureDeltaTimeCorrect() {

        var timeCounter = 0L
        val delta = 1000L
        val math = StatisticsMathProvider({ timeCounter += delta; timeCounter })

        math.measureDeltaTime()
        assertEquals(delta, math.measureDeltaTime())
        assertEquals(delta, math.measureDeltaTime())
    }

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