package ga.lupuss.anotherbikeapp.ui.modules.main

import android.content.ComponentName
import android.content.Intent
import android.support.transition.Slide
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.Button
import android.widget.ListView
import android.widget.TextView
import com.nhaarman.mockito_kotlin.*
import ga.lupuss.anotherbikeapp.R
import ga.lupuss.anotherbikeapp.TestAnotherBikeApp
import ga.lupuss.anotherbikeapp.models.android.AndroidTrackingServiceGovernor
import ga.lupuss.anotherbikeapp.replaceComponentInActivityController
import ga.lupuss.anotherbikeapp.ui.adapters.DrawerListViewAdapter
import ga.lupuss.anotherbikeapp.ui.adapters.RoutesHistoryRecyclerViewAdapter
import ga.lupuss.anotherbikeapp.ui.modules.settings.SettingsActivity
import ga.lupuss.anotherbikeapp.ui.modules.summary.SummaryActivity
import ga.lupuss.anotherbikeapp.ui.modules.summary.SummaryPresenter
import ga.lupuss.anotherbikeapp.ui.modules.tracking.TrackingActivity
import ga.lupuss.anotherbikeapp.ui.modules.tracking.TrackingPresenter
import junit.framework.Assert
import junit.framework.TestCase.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowDialog

@RunWith(RobolectricTestRunner::class)
@Config(application = TestAnotherBikeApp::class)
class MainActivityTest {

    private val activity = Robolectric.setupActivity(MainActivity::class.java)

    @Test
    fun isNoDataTextVisible_whenTrue_shouldShowNoDataText() {

        val view = activity.findViewById<TextView>(R.id.noDataText)
        activity.isNoDataTextVisible = true

        Assert.assertEquals(View.VISIBLE, view.visibility)
    }

    @Test
    fun isNoDataTextVisible_whenFalse_shouldHideNoDataText() {

        val view = activity.findViewById<TextView>(R.id.noDataText)
        activity.isNoDataTextVisible = false

        Assert.assertEquals(View.INVISIBLE, view.visibility)
    }

    @Test
    fun isRoutesHistoryVisible_whenTrue_shouldShowRoutesHistoryRecycler() {

        val view = activity.findViewById<View>(R.id.routesHistoryRecycler)
        activity.isRoutesHistoryVisible = true

        Assert.assertEquals(View.VISIBLE, view.visibility)
    }

    @Test
    fun isRoutesHistoryVisible_whenFalse_shouldHideRoutesHistoryRecycler() {

        val view = activity.findViewById<View>(R.id.routesHistoryRecycler)
        activity.isRoutesHistoryVisible = false

        Assert.assertEquals(View.INVISIBLE, view.visibility)
    }

    @Test
    fun isProgressBarVisible_whenTrue_shouldShowProgressBar() {

        val view = activity.findViewById<View>(R.id.recyclerProgressBar)
        activity.isProgressBarVisible = true

        Assert.assertEquals(View.VISIBLE, view.visibility)
    }

    @Test
    fun isProgressBarVisible_whenFalse_shouldHideProgressBar() {

        val view = activity.findViewById<View>(R.id.recyclerProgressBar)
        activity.isProgressBarVisible = false

        Assert.assertEquals(View.GONE, view.visibility)
    }

    @Test
    fun isDrawerLayoutOpened_shouldReturnProperDrawerState() {
        val view = activity.findViewById<DrawerLayout>(R.id.drawerLayout)

        view.openDrawer(GravityCompat.START, false)
        Assert.assertEquals(true, activity.isDrawerLayoutOpened)

        view.closeDrawer(GravityCompat.START, false)
        Assert.assertEquals(false, activity.isDrawerLayoutOpened)
    }

    @Test
    fun onCreate_shouldInitStuff() {

        val controller = Robolectric.buildActivity(MainActivity::class.java)

        val activity =  spy(controller.get())

        replaceComponentInActivityController(controller, activity)

        controller.create()

        verify(activity.trackingServiceGovernor as AndroidTrackingServiceGovernor,
                times(1)).init(eq(activity), anyOrNull())
        verify(activity.mainPresenter, times(1)).notifyOnViewReady()
        verify(activity, times(1)).setContentView(R.layout.activity_main)
        verify(activity, times(1)).activateToolbar(any(), any())

        val drawerListView = activity.findViewById<ListView>(R.id.drawerListView)

        Assert.assertEquals(DrawerListViewAdapter::class.java, drawerListView.adapter::class.java)
        Assert.assertEquals(activity, drawerListView.onItemClickListener)

        val recyclerView = activity.findViewById<RecyclerView>(R.id.routesHistoryRecycler)

        Assert.assertEquals(false, recyclerView.isNestedScrollingEnabled)
        Assert.assertEquals(RoutesHistoryRecyclerViewAdapter::class.java, recyclerView.adapter::class.java)
        Assert.assertEquals(LinearLayoutManager::class.java, recyclerView.layoutManager::class.java)
    }

    @Test
    fun onNewIntent_shouldNotifyPresenter() {

        val controller = Robolectric.buildActivity(MainActivity::class.java)
        val activity = controller.create().start().resume().newIntent(mock{}).get()

        verify(activity.mainPresenter, times(1))
                .notifyOnResult(MainPresenter.Request.TRACKING_NOTIFICATION_REQUEST, 0)
    }

    @Test
    fun onActivityResult_shouldNotifyPresenter() {

        val controller = Robolectric.buildActivity(MainActivity::class.java)
        val activity = controller.create().start().resume().get()

        activity.startTrackingActivity()

        Shadows.shadowOf(activity).receiveResult(
                Intent(activity, TrackingActivity::class.java),
                TrackingPresenter.Result.DONE,
                null
        )


        verify(activity.mainPresenter, times(1))
                .notifyOnResult(MainPresenter.Request.TRACKING_ACTIVITY_REQUEST, TrackingPresenter.Result.DONE)
    }

    @Test
    fun onSaveInstanceState_shouldDelegateToAndroidServiceGovernor() {

        val activity = Robolectric.buildActivity(MainActivity::class.java)
                .create()
                .start()
                .resume()
                .saveInstanceState(mock {  })
                .get()

        verify(activity.trackingServiceGovernor as AndroidTrackingServiceGovernor, times(1)).saveInstanceState(any())
    }

    @Test
    fun onBackPressed_shouldNotifyPresenter() {
        val activity = Robolectric.buildActivity(MainActivity::class.java)
                .create()
                .start()
                .resume()
                .get()
        activity.onBackPressed()

        verify(activity.mainPresenter, times(1)).onExitRequest()
    }

    @Test
    fun finishActivity_shouldFinishActivityProperly() {

        val controller = Robolectric.buildActivity(MainActivity::class.java)
                .create()
                .start()
                .resume()

        val activity = spy(controller.get())

        activity.finishActivity()
        verify(activity, times(1)).finishFromChild(activity.parent)
        verify(activity, times(1)).finishAndRemoveTask()
    }

    @Test
    fun onDestroy_shouldNotifyPresenterAndGovernor() {

        val activity = Robolectric.buildActivity(MainActivity::class.java)
                .create()
                .start()
                .resume()
                .pause()
                .stop()
                .destroy()
                .get()

        verify(activity.mainPresenter, times(1)).notifyOnDestroy(any())
        verify(activity.trackingServiceGovernor as AndroidTrackingServiceGovernor, times(1)).destroy(any())
    }

    @Test
    fun onClickTrackingButton_shouldNotifyPresenter() {

        activity.onClickTrackingButton(mock {  }).end()

        verify(activity.mainPresenter, times(1)).onClickTrackingButton()
    }

    @Test
    fun onRecyclerItemClick_shouldNotifyPresenter() {

        activity.findViewById<RecyclerView>(R.id.routesHistoryRecycler).apply {
            measure(0,0)
            layout(0, 0, 100, 10000)
            findViewHolderForAdapterPosition(0).itemView.performClick()
        }

        verify(activity.mainPresenter, times(1)).onClickShortRoute(0)
    }

    @Test
    fun onDrawerItemClick_whenFirstItem_shouldNotifyPresenterAboutSignOutClick() {
        val drawer = activity.findViewById<ListView>(R.id.drawerListView)
        val item = drawer.adapter.getItem(0)

        Shadows.shadowOf(drawer).performItemClick(0)

        if (item is Pair<*, *> && item.first is MainActivity.ItemName) {

            Assert.assertEquals(MainActivity.ItemName.SIGN_OUT, item.first)
            verify(activity.mainPresenter, times(1)).onClickSignOut()
            verify(activity.mainPresenter, never()).onClickSettings()

        } else if (item is Pair<*, *>) {
            Assert.fail("item.first is not ItemName (${item.first!!::class.java.canonicalName}")
        } else {
            Assert.fail("Item is not Pair (${item::class.java.canonicalName}")
        }
    }

    @Test
    fun onDrawerItemClick_whenSecondItem_shouldNotifyPresenterAboutSettingsClick() {

        val drawer = activity.findViewById<ListView>(R.id.drawerListView)
        val item = drawer.adapter.getItem(1)

        Shadows.shadowOf(drawer).performItemClick(1)

        if (item is Pair<*, *> && item.first is MainActivity.ItemName) {

            Assert.assertEquals(MainActivity.ItemName.SETTINGS, item.first)
            verify(activity.mainPresenter, never()).onClickSignOut()
            verify(activity.mainPresenter, times(1)).onClickSettings()

        } else if (item is Pair<*, *>) {
            Assert.fail("item.first is not ItemName (${item.first!!::class.java.canonicalName}")
        } else {
            Assert.fail("Item is not Pair (${item::class.java.canonicalName}")
        }
    }

    @Test
    fun hideDrawer_shouldHideDrawer() {

        activity.hideDrawer()

        Assert.assertEquals(false,
                activity.findViewById<DrawerLayout>(R.id.drawerLayout).isDrawerOpen(GravityCompat.START))
    }

    @Test
    fun setTrackingButtonState_whenTrue_shouldSetContinueTrackingText() {

        activity.setTrackingButtonState(true)

        Assert.assertEquals(activity.getString(R.string.continueTracking), activity.findViewById<Button>(R.id.trackingButton).text)
    }

    @Test
    fun setTrackingButtonState_whenFalse_shouldSetContinueTrackingText() {

        activity.setTrackingButtonState(false)

        assertEquals(activity.getString(R.string.startTracking), activity.findViewById<Button>(R.id.trackingButton).text)
    }

    @Test
    fun refreshRecyclerAdapter_shouldNotifyDataSetChanged() {

        val mockedAdapter = mock<RoutesHistoryRecyclerViewAdapter> {}
        activity.findViewById<RecyclerView>(R.id.routesHistoryRecycler).adapter = mockedAdapter

        activity.refreshRecyclerAdapter()

        verify(mockedAdapter, times(1)).notifyDataSetChanged()
    }

    @Test
    fun notifyRecyclerItemChanged_shouldNotifyRecycler() {

        val mockedAdapter = mock<RoutesHistoryRecyclerViewAdapter> {}
        activity.findViewById<RecyclerView>(R.id.routesHistoryRecycler).adapter = mockedAdapter

        activity.notifyRecyclerItemChanged(0)

        verify(mockedAdapter, times(1)).notifyItemChanged(0)
    }

    @Test
    fun notifyRecyclerItemRemoved_shouldNotifyRecycler() {

        val mockedAdapter = mock<RoutesHistoryRecyclerViewAdapter> {}
        activity.findViewById<RecyclerView>(R.id.routesHistoryRecycler).adapter = mockedAdapter

        activity.notifyRecyclerItemRemoved(0, 1)

        verify(mockedAdapter, times(1)).notifyItemRemoved(0)
        verify(mockedAdapter, times(1)).notifyItemRangeChanged(0, 1)
    }

    @Test
    fun notifyRecyclerItemInserted_shouldNotifyRecycler() {

        val mockedAdapter = mock<RoutesHistoryRecyclerViewAdapter> {}
        activity.findViewById<RecyclerView>(R.id.routesHistoryRecycler).adapter = mockedAdapter

        activity.notifyRecyclerItemInserted(0, 1)

        verify(mockedAdapter, times(1)).notifyItemInserted(0)
        verify(mockedAdapter, times(1)).notifyItemRangeChanged(0, 1)
    }

    @Test
    fun setDrawerHeaderInfo_whenDisplayNameNotNullAndNotBlank_shouldSetHeaderProperly() {

        activity.setDrawerHeaderInfo("Name", "email@gmail.com")

        val header = Shadows.shadowOf(activity.findViewById<ListView>(R.id.drawerListView)).headerViews[0]

        assertEquals("Name", header.findViewById<TextView>(R.id.userName).text)
        assertEquals("email@gmail.com", header.findViewById<TextView>(R.id.userEmail).text)
    }

    @Test
    fun setDrawerHeaderInfo_whenDisplayNameIsBlank_shouldSetHeaderProperlyWithDefaultUserName() {

        activity.setDrawerHeaderInfo("", "email@gmail.com")

        val header = Shadows.shadowOf(activity.findViewById<ListView>(R.id.drawerListView)).headerViews[0]

        assertEquals(activity.getString(R.string.user), header.findViewById<TextView>(R.id.userName).text)
        assertEquals("email@gmail.com", header.findViewById<TextView>(R.id.userEmail).text)
    }

    @Test
    fun setDrawerHeaderInfo_whenDisplayNameIsNull_shouldSetHeaderProperlyWithDefaultUserName() {

        activity.setDrawerHeaderInfo(null, "email@gmail.com")

        val header = Shadows.shadowOf(activity.findViewById<ListView>(R.id.drawerListView)).headerViews[0]

        assertEquals(activity.getString(R.string.user), header.findViewById<TextView>(R.id.userName).text)
        assertEquals("email@gmail.com", header.findViewById<TextView>(R.id.userEmail).text)
    }

    @Test
    fun setDrawerHeaderInfo_whenEmailIsNull_shouldNotCauseError() {

        activity.setDrawerHeaderInfo(null, null)
    }

    @Test
    fun showExitWarningDialog_shouldShowDialog() {

        var triggered = 0
        activity.showExitWarningDialog {
            triggered++
        }

        val dialog = ShadowDialog.getLatestDialog() as AlertDialog

        assert(dialog.isShowing)

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).performClick()

        // Title and message should be tested too, but AFAIK it's not possible with android.support.v7.app.AlertDialog
        // android.app.AlertDialog cannot be used, because of styles problem

        assertEquals(1, triggered)
        assertEquals(activity.getString(R.string.exit), dialog.getButton(AlertDialog.BUTTON_POSITIVE).text)
        assertEquals(activity.getString(R.string.cancel), dialog.getButton(AlertDialog.BUTTON_NEGATIVE).text)
    }

    @Test
    fun startTrackingActivity_shouldStartTrackingActivityForResult() {

        activity.startTrackingActivity()
        val shadow = Shadows.shadowOf(activity)

        assertEquals(ComponentName(activity, TrackingActivity::class.java),
                shadow.peekNextStartedActivityForResult().intent.component)
    }

    @Test
    fun startSettingsActivity_shouldStartSettingsActivity() {

        activity.startSettingsActivity()
        val shadow = Shadows.shadowOf(activity)

        assertEquals(ComponentName(activity, SettingsActivity::class.java),
                shadow.peekNextStartedActivity().component)
    }

    @Test
    fun startSummaryActivity_whenNoArguments_shouldStartSummaryActivity() {

        activity.startSummaryActivity()
        val shadow = Shadows.shadowOf(activity)

        val nextActivity = shadow.peekNextStartedActivity()

        assertEquals(ComponentName(activity, SummaryActivity::class.java), nextActivity.component)

        val bundle = nextActivity.extras
        assertNull(bundle.getString(SummaryActivity.DOC_REFERENCE_KEY))
        assertEquals(SummaryPresenter.Mode.AFTER_TRACKING_SUMMARY.toString(), bundle.getString(SummaryActivity.MODE_KEY))
    }

    @Test
    fun startSummaryActivity_whenDocReferenceArgument_shouldStartSummaryActivity() {

        activity.startSummaryActivity("just string")
        val shadow = Shadows.shadowOf(activity)

        val nextActivity = shadow.peekNextStartedActivity()
        val bundle = nextActivity.extras
        assertNotNull(bundle.getString(SummaryActivity.DOC_REFERENCE_KEY))
        assertEquals(SummaryPresenter.Mode.OVERVIEW.toString(), bundle.getString(SummaryActivity.MODE_KEY))
        assertEquals(ComponentName(activity, SummaryActivity::class.java), nextActivity.component)
    }
}