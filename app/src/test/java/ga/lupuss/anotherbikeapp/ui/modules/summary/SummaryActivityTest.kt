package ga.lupuss.anotherbikeapp.ui.modules.summary

import android.widget.EditText
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.UiSettings
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.nhaarman.mockito_kotlin.*
import ga.lupuss.anotherbikeapp.R
import ga.lupuss.anotherbikeapp.TestAnotherBikeApp
import ga.lupuss.anotherbikeapp.ui.extensions.ViewExtensions
import ga.lupuss.anotherbikeapp.ui.extensions.getColorForAttr
import junit.framework.Assert
import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(application = TestAnotherBikeApp::class)
class SummaryActivityTest {

    private fun getController(docRef: String?) =
            Robolectric.buildActivity(
                    SummaryActivity::class.java,
                    if (docRef != null)
                        SummaryActivity.newIntent(mock { }, docRef)
                    else
                        SummaryActivity.newIntent(mock { })
            )

    private val activity = getController(null).create().visible().get()

    @After
    fun validate() {
        validateMockitoUsage()
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
    fun onCreateOptionsMenu_whenAfterTracking_shouldShowOrHideItems() {

        Assert.assertEquals(
                activity.isRejectActionVisible,
                Shadows.shadowOf(activity).optionsMenu.findItem(R.id.itemRejectRoute).isVisible
        )

        Assert.assertEquals(
                activity.isSaveActionVisible,
                Shadows.shadowOf(activity).optionsMenu.findItem(R.id.itemSaveRoute).isVisible
        )
    }

    @Test
    fun onCreateOptionsMenu_whenOverview_shouldShowOrHideItems() {

        Assert.assertEquals(
                activity.isRejectActionVisible,
                Shadows.shadowOf(activity).optionsMenu.findItem(R.id.itemRejectRoute).isVisible
        )

        Assert.assertEquals(
                activity.isSaveActionVisible,
                Shadows.shadowOf(activity).optionsMenu.findItem(R.id.itemSaveRoute).isVisible
        )
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
    fun showRouteLine_shouldSetLineProperly() {
        val mapMock = mock<GoogleMap> { on { uiSettings }.then { mock<UiSettings> {} } }

        val list = listOf(LatLng(0.0, 0.0))
        activity.onMapReady(mapMock)

        val icon = ViewExtensions
                .getDefaultMarkerIconForColor(activity.theme.getColorForAttr(R.attr.markersColor))
        activity.showRouteLine(list)
        verify(mapMock, times(1)).addMarker(
                MarkerOptions().title(activity.getString(R.string.start))
                        .icon(icon)
                        .position(list.first())
        )
    }

}