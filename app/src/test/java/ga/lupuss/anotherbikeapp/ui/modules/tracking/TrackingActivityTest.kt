package ga.lupuss.anotherbikeapp.ui.modules.tracking

import android.view.View
import android.widget.ImageButton
import com.nhaarman.mockito_kotlin.mock
import ga.lupuss.anotherbikeapp.R
import ga.lupuss.anotherbikeapp.TestAnotherBikeApp
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(application = TestAnotherBikeApp::class)
class TrackingActivityTest {


    private val intent = TrackingActivity.newIntent(mock {  }, mock {  })
    private val trackingActivity = Robolectric.buildActivity(TrackingActivity::class.java, intent)
            .create().start().resume().visible().get()

    @Test
    fun onCreate_whenFirstStart_shouldDoProperInitStuff() {

        val controller = Robolectric.buildActivity(TrackingActivity::class.java, intent).create(null)
        val trackingActivity = controller.get()

        val statsContainer = trackingActivity.findViewById<View>(R.id.statsContainer)
        val statsContainerExpandButton = trackingActivity.findViewById<View>(R.id.statsContainerExpandButton)
        val statsContainerExpandButtonIcon = trackingActivity.findViewById<View>(R.id.statsContainerExpandButtonIcon)
        val shortStatsContainer = trackingActivity.findViewById<View>(R.id.shortStatsContainer)

        assertEquals(TrackingPresenter.Result.NOT_DONE, Shadows.shadowOf(trackingActivity).resultCode)
        assertEquals(View.INVISIBLE, statsContainer.visibility)
        assertEquals(View.INVISIBLE, statsContainerExpandButton.visibility)
        assertEquals(View.INVISIBLE, statsContainerExpandButtonIcon.visibility)
        assertEquals(0F, shortStatsContainer.alpha)
        assertEquals(true, trackingActivity.isMapButtonInLockState)
    }

    @Test
    fun isInfoWaitForLocationVisible_whenTrue_shouldSetVisibilityToVisible() {

        trackingActivity.isInfoWaitForLocationVisible = true
        assertEquals(View.VISIBLE ,trackingActivity.findViewById<View>(R.id.infoWaitForLocation).visibility)
        assertEquals(true, trackingActivity.isInfoWaitForLocationVisible)
    }

    @Test
    fun isInfoWaitForLocationVisible_whenFalse_shouldSetVisibilityToInvisible() {

        trackingActivity.isInfoWaitForLocationVisible = false
        assertEquals(View.INVISIBLE ,trackingActivity.findViewById<View>(R.id.infoWaitForLocation).visibility)
        assertEquals(false, trackingActivity.isInfoWaitForLocationVisible)
    }

    @Test
    fun isMapButtonInLockState_whenTrue_shouldSetProperLockedDrawables() {

        trackingActivity.isMapButtonInLockState = true
        val view = trackingActivity.findViewById<ImageButton>(R.id.mapLockButton)
        val shadow = Shadows.shadowOf(view.drawable)
        val shadowBack = Shadows.shadowOf(view.background)

        assertEquals(R.drawable.ic_lock_24dp, shadow.createdFromResId)
        assertEquals(R.drawable.button_lock_back, shadowBack.createdFromResId)
        assertEquals(true, trackingActivity.isMapButtonInLockState)
    }

    @Test
    fun isMapButtonInLockState_whenFalse_shouldSetUnlockedDrawables() {

        trackingActivity.isMapButtonInLockState = false
        val view = trackingActivity.findViewById<ImageButton>(R.id.mapLockButton)
        val shadow = Shadows.shadowOf(view.drawable)
        val shadowBack = Shadows.shadowOf(view.background)

        assertEquals(R.drawable.ic_unlock_24dp, shadow.createdFromResId)
        assertEquals(R.drawable.button_unlock_back, shadowBack.createdFromResId)
        assertEquals(false, trackingActivity.isMapButtonInLockState)
    }

}