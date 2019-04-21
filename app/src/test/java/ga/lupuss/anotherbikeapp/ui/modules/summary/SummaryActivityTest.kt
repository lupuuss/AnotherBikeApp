package ga.lupuss.anotherbikeapp.ui.modules.summary

import androidx.appcompat.app.AlertDialog
import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.google.android.gms.maps.UiSettings
import com.nhaarman.mockito_kotlin.*
import ga.lupuss.anotherbikeapp.R
import ga.lupuss.anotherbikeapp.TestAnotherBikeApp
import org.junit.After
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowDialog

@RunWith(RobolectricTestRunner::class)
@Config(application = TestAnotherBikeApp::class, sdk = [27])
class SummaryActivityTest {

    private fun getController(docRef: String?) =
            Robolectric.buildActivity(
                    SummaryActivity::class.java,
                    if (docRef != null)
                        SummaryActivity.newIntent(mock { }, docRef)
                    else
                        SummaryActivity.newIntent(mock { })
            )

    private val activity = getController(null).setup().get()

    @After
    fun validate() {
        validateMockitoUsage()
    }

    @Test
    fun isRouteEditLineVisible_whenTrue_shouldShowRouteEditLine() {

        activity.isRouteEditLineVisible = false
        activity.isRouteEditLineVisible = true

        val nameLabel = activity.findViewById<TextView>(R.id.nameLabel)
        val routeNameEdit = activity.findViewById<EditText>(R.id.routeNameEdit)

        Assert.assertEquals(View.VISIBLE, nameLabel.visibility)
        Assert.assertEquals(View.VISIBLE, routeNameEdit.visibility)
    }

    @Test
    fun isRouteEditLineVisible_whenFalse_shouldHideRouteEditLine() {

        activity.isRouteEditLineVisible = true
        activity.isRouteEditLineVisible = false

        val nameLabel = activity.findViewById<TextView>(R.id.nameLabel)
        val routeNameEdit = activity.findViewById<EditText>(R.id.routeNameEdit)

        Assert.assertEquals(View.INVISIBLE, nameLabel.visibility)
        Assert.assertEquals(View.INVISIBLE, routeNameEdit.visibility)
    }

    @Test
    fun isStatsFragmentVisible_whenTrue_shouldShowStatsFragment() {

        activity.isStatsFragmentVisible = false
        activity.isStatsFragmentVisible = true

        val statsFragmentWrapper = activity.findViewById<View>(R.id.routeInfoContainer)

        Assert.assertEquals(View.VISIBLE, statsFragmentWrapper.visibility)
    }

    @Test
    fun isStatsFragmentVisible_whenFalse_shouldHideStatsFragment() {

        activity.isStatsFragmentVisible = true
        activity.isStatsFragmentVisible = false

        val statsFragmentWrapper = activity.findViewById<View>(R.id.routeInfoContainer)

        Assert.assertEquals(View.INVISIBLE, statsFragmentWrapper.visibility)
    }

    @Test
    fun isProgressBarVisible_whenTrue_shouldShowProgressBar() {

        activity.isProgressBarVisible = false
        activity.isProgressBarVisible = true

        val progressBar = activity.findViewById<View>(R.id.summaryProgressBar)

        Assert.assertEquals(View.VISIBLE, progressBar.visibility)
    }

    @Test
    fun isProgressBarVisible_whenFalse_shouldShowProgressBar() {

        activity.isProgressBarVisible = true
        activity.isProgressBarVisible = false

        val progressBar = activity.findViewById<View>(R.id.summaryProgressBar)

        Assert.assertEquals(View.INVISIBLE, progressBar.visibility)
    }

    @Test
    fun onCreate_whenModeAfterTracking_shouldInitStuffProperly() {

        verify(activity.summaryPresenter, times(1))
                .initMode(SummaryPresenter.Mode.AFTER_TRACKING_SUMMARY, null)
    }

    @Test
    fun onMapReady_shouldNotifyPresenter() {

        activity.onMapReady(mock{
            on { uiSettings }.then { mock<UiSettings> { } }
        })

        verify(activity.summaryPresenter, times(1))
                .notifyOnViewReady()
    }

    @Test
    fun onBackPressed_shouldDelegateToPresenter() {

        activity.onBackPressed()
        verify(activity.summaryPresenter, times(1)).onExitRequest()
    }

    @Test
    fun onDestroy_shouldDelegateToPresenter() {

        val activity = getController(null).create().destroy().get()

        verify(activity.summaryPresenter, times(1)).notifyOnDestroy(any())
    }

    @Test
    fun onTextChanged_shouldDelegateToPresenter() {

        activity.onTextChanged("text", 0, 0, 0)

        verify(activity.summaryPresenter, times(1)).onNameEditTextChanged(eq("text"))
    }

    @Test
    fun onSaveClick_shouldDelegateToPresenter() {

        activity.onSaveClick(null)

        verify(activity.summaryPresenter, times(1)).onSaveClick()
    }

    @Test
    fun onRejectClick_shouldDelegateToPresenter() {

        activity.onRejectClick(null)

        verify(activity.summaryPresenter, times(1)).onRejectClick()
    }

    @Test
    fun setNameLabelValue_shouldChangeRouteNameEditText() {

        activity.setNameLabelValue("Label")
        val view = activity.findViewById<EditText>(R.id.routeNameEdit)

        Assert.assertEquals("Label", view.text.toString())
    }

    @Test
    fun showRejectDialog_shouldShowDialog() {
        var triggered = 0

        activity.showRejectDialog {
            triggered++
        }

        val dialog =  ShadowDialog.getLatestDialog() as AlertDialog

        Assert.assertEquals(true, dialog.isShowing)


        dialog.getButton(AlertDialog.BUTTON_POSITIVE).performClick()

        // Title and message should be tested too, but AFAIK it's not possible with android.support.v7.app.AlertDialog
        // android.app.AlertDialog cannot be used, because of styles problem

        Assert.assertEquals(1, triggered)
        Assert.assertEquals(activity.getString(R.string.reject), dialog.getButton(AlertDialog.BUTTON_POSITIVE).text)
        Assert.assertEquals(activity.getString(R.string.cancel), dialog.getButton(AlertDialog.BUTTON_NEGATIVE).text)
    }

    @Test
    fun showDeleteDialog_shouldShowDialog() {
        var triggered = 0

        activity.showDeleteDialog {
            triggered++
        }

        val dialog =  ShadowDialog.getLatestDialog() as AlertDialog

        Assert.assertEquals(true, dialog.isShowing)


        dialog.getButton(AlertDialog.BUTTON_POSITIVE).performClick()

        // Title and message should be tested too, but AFAIK it's not possible with android.support.v7.app.AlertDialog
        // android.app.AlertDialog cannot be used, because of styles problem

        Assert.assertEquals(1, triggered)
        Assert.assertEquals(activity.getString(R.string.delete), dialog.getButton(AlertDialog.BUTTON_POSITIVE).text)
        Assert.assertEquals(activity.getString(R.string.cancel), dialog.getButton(AlertDialog.BUTTON_NEGATIVE).text)
    }

    @Test
    fun showUnsavedStateDialog_shouldShowDialog() {
        var triggered = 0

        activity.showUnsavedStateDialog {
            triggered++
        }

        val dialog =  ShadowDialog.getLatestDialog() as AlertDialog

        Assert.assertEquals(true, dialog.isShowing)


        dialog.getButton(AlertDialog.BUTTON_POSITIVE).performClick()

        // Title and message should be tested too, but AFAIK it's not possible with android.support.v7.app.AlertDialog
        // android.app.AlertDialog cannot be used, because of styles problem

        Assert.assertEquals(1, triggered)
        Assert.assertEquals(activity.getString(R.string.exit), dialog.getButton(AlertDialog.BUTTON_POSITIVE).text)
        Assert.assertEquals(activity.getString(R.string.cancel), dialog.getButton(AlertDialog.BUTTON_NEGATIVE).text)
    }

    @Test
    fun getRouteNameFromEditText_shouldReturnProperValueFromView() {

        activity.findViewById<TextView>(R.id.routeNameEdit).text = "TestString"

        Assert.assertEquals("TestString", activity.getRouteNameFromEditText())
    }

}