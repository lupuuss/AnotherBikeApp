package ga.lupuss.anotherbikeapp.trackingservice.statisticsmanager

import android.location.Location
import android.os.Handler
import com.google.android.gms.maps.model.LatLng
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import ga.lupuss.anotherbikeapp.trackingservice.statisticsmanager.statistics.Statistic
import junit.framework.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import java.util.*


class StatisticsManagerTest {

    val handler = mock<Handler> {
        on { postDelayed(any(), any()) }.then { true }
    }

    val mSingleLocation = mockLocation(52.237049, 21.017532)

    lateinit var statisticsManager: StatisticsManager

    var lastTime = 0L

    private fun mockLocation(lat: Double, lng: Double, alt: Double = 0.0): Location {

        return mock {
            on { latitude }.thenReturn(lat)
            on { longitude }.thenReturn(lng)
            on { altitude }.thenReturn(alt)
        }
    }

    private fun mockLatLng(lat: Double, lng: Double): LatLng {
        return LatLng(lat, lng)
    }

    @Before
    fun setUp() {
        statisticsManager = StatisticsManager(
                Locale.GERMAN,
                Timer(handler),
                StatisticsMathProvider { lastTime += 1000; lastTime }
        )
    }

    @Test
    fun passedLocation_shouldBeKeptAsLastLocation() {

        statisticsManager.pushNewLocation(mSingleLocation)

        assertEquals(mSingleLocation, statisticsManager.lastLocation)
    }

    @Test
    fun passedSameLocation_shouldNotBeSaved() {

        for (i in 1..5) {
            statisticsManager.pushNewLocation(mSingleLocation)
        }

        assertEquals(1, statisticsManager.savedRoute.size)
    }

    @Test
    fun afterPrecisePushesCount_shouldBePreciseStatisticsUpdatesCount() {

        var newStatsCounter = 0

        statisticsManager.onNewStats = { newStatsCounter++ }

        statisticsManager.pushNewLocation(mSingleLocation)

        assertEquals(1, newStatsCounter)

        val rotatesCount = 2

        for (i in 1..(rotatesCount * statisticsManager.minElementsToCount)) {

            statisticsManager.pushNewLocation(mSingleLocation)
        }
        assertEquals(1 + rotatesCount, newStatsCounter)
    }

    @Test
    fun firstPush_shouldStartTimer() {
        assertEquals(false, statisticsManager.timer.isStarted)
        statisticsManager.pushNewLocation(mSingleLocation)
        assertEquals(true, statisticsManager.timer.isStarted)
    }

    @Test
    fun notifyingLostConnection_shouldChangeStateToPause() {

        statisticsManager.pushNewLocation(mSingleLocation) // init timer
        statisticsManager.notifyLostLocation()
        assertEquals(Status.LOCATION_WAIT, statisticsManager.status)
        assertEquals(true, statisticsManager.timer.isPaused)
    }

    @Test
    fun lowSpeed_shouldChangeStateToPause() {

        statisticsManager.pushNewLocation(mSingleLocation)
        statisticsManager.notifyLocationOk()

        simulateLowSpeed(statisticsManager)

        assertEquals(Status.PAUSE, statisticsManager.status)
        assertEquals(true, statisticsManager.timer.isPaused)
    }

    @Test
    fun highSpeed_shouldChangeStateToRunning() {

        statisticsManager.pushNewLocation(mSingleLocation)
        statisticsManager.onNewStats = {
            println(it[Statistic.Name.SPEED]!!.getValue(mock {}))
        }
        statisticsManager.notifyLocationOk()

        simulateLowSpeed(statisticsManager)

        simulateHighSpeed(statisticsManager)

        assertEquals(Status.RUNNING, statisticsManager.status)
        assertEquals(false, statisticsManager.timer.isPaused)
    }

    private fun simulateLowSpeed(statisticsManager: StatisticsManager) {
        for (i in 1..5) {
            statisticsManager.pushNewLocation(mSingleLocation)
        }
    }

    private fun simulateHighSpeed(statisticsManager: StatisticsManager) {

        var lat = 0.0
        var lng = 0.0

        for (i in 1..5) {
            lat += 1
            lng += 1
            statisticsManager.pushNewLocation(mockLocation(lat, lng))
        }
    }
}