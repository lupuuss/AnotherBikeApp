package ga.lupuss.anotherbikeapp.ui.modules.tracking
import com.google.android.gms.maps.model.LatLng
import com.nhaarman.mockito_kotlin.*
import ga.lupuss.anotherbikeapp.Message
import ga.lupuss.anotherbikeapp.models.base.TrackingServiceInteractor
import ga.lupuss.anotherbikeapp.models.dataclass.Statistic
import org.junit.Test

class TrackingPresenterTest {

    private val list = listOf(LatLng(0.0, 0.0))
    private val map = mapOf< Statistic.Name, Statistic<*>>()
    private val trackingView: TrackingView = mock {

        on { it.showFinishTrackingDialog(any()) }.then { (it.getArgument(0) as () -> Unit).invoke() }
    }

    @Test
    fun notifyOnViewReady_shouldPrepareViewBasesOnServiceInteractorState_emptyInteractorData() {

        val serviceInteractor: TrackingServiceInteractor =  mock {
            on { savedRoute }.then { emptyList<LatLng>() }
            on { lastStats }.then { null }
        }

        val trackingPresenter = TrackingPresenter(trackingView, serviceInteractor)

        trackingPresenter.notifyOnViewReady()

        verify(trackingView, never()).prepareMapToTrack(any())
        verify(trackingView, never()).updateStats(any())
        verifyServiceInteractorListeners(serviceInteractor, trackingPresenter)
        verifyTrackingViewChanges(serviceInteractor)
    }

    @Test
    fun notifyOnViewReady_shouldPrepareViewBasesOnServiceInteractorState_filledInteractorData() {

        val serviceInteractor: TrackingServiceInteractor = mock {
            on { savedRoute }.then { list }
            on { lastStats }.then { map }
        }

        val trackingPresenter = TrackingPresenter(trackingView, serviceInteractor)

        trackingPresenter.notifyOnViewReady()

        verify(trackingView, times(1)).prepareMapToTrack(list)
        verify(trackingView, times(1)).updateStats(map)
        verifyServiceInteractorListeners(serviceInteractor, trackingPresenter)
        verifyTrackingViewChanges(serviceInteractor)

    }

    private fun verifyServiceInteractorListeners(serviceInteractor: TrackingServiceInteractor,
                                                 trackingPresenter: TrackingPresenter) {
        verify(serviceInteractor, times(1)).addOnStatsUpdateListener(trackingPresenter)
        verify(serviceInteractor, times(1)).connectServiceDataReceiver(trackingPresenter)
    }

    private fun verifyTrackingViewChanges(serviceInteractor: TrackingServiceInteractor) {

        verify(trackingView, times(1)).mapLockButtonState = trackingView.mapLockButtonState
        verify(trackingView, times(1)).isInfoWaitForLocationVisible = !serviceInteractor.lastLocationAvailability
    }

    @Test
    fun onClickLockMap_whenRouteEnable_shouldChangeTrackingView() {

        val trackingPresenter = TrackingPresenter(trackingView, mock { on { savedRoute }.then { list } })

        trackingPresenter.onClickLockMap()
        verify(trackingView, times(1)).mapLockButtonState = false

        trackingPresenter.onClickLockMap()
        verify(trackingView, times(1)).mapLockButtonState = true
        verify(trackingView, times(1)).moveMapCamera(list.last())

    }

    @Test
    fun onClickLockMap_whenRouteNotEnable_shouldChangeTrackingView() {

        val trackingPresenter = TrackingPresenter(trackingView, mock { on { savedRoute }.then { emptyList<LatLng>() } })

        trackingPresenter.onClickLockMap()
        verify(trackingView, times(1)).mapLockButtonState = false

        trackingPresenter.onClickLockMap()
        verify(trackingView, times(1)).mapLockButtonState = true
        verify(trackingView, never()).moveMapCamera(any())
    }

    @Test
    fun onClickFinishTracking_whenServiceIsNotInProgress_shouldFinishActivity() {

        val serviceInteractor: TrackingServiceInteractor = mock { on { isServiceInProgress() }.then { false }}
        val trackingPresenter = TrackingPresenter(trackingView, serviceInteractor)

        trackingPresenter.onClickFinishTracking()

        verify(trackingView, times(1)).finishActivityWithResult(TrackingPresenter.Result.NO_DATA_DONE)
        verify(trackingView, never()).showFinishTrackingDialog(any())
    }

    @Test
    fun onClickFinishTracking_whenServiceInProgress_shouldShowWarningDialogAndFinishActivity() {

        val serviceInteractor: TrackingServiceInteractor = mock { on { isServiceInProgress() }.then { true }}
        val trackingPresenter = TrackingPresenter(trackingView, serviceInteractor)

        trackingPresenter.onClickFinishTracking()

        verify(trackingView, times(1)).showFinishTrackingDialog(any())
        verify(trackingView, times(1)).finishActivityWithResult(TrackingPresenter.Result.DONE)

    }

    @Test
    fun onNewLocation_whenTrackLineReady_shouldUpdateTrackingView() {

        // Track line is not ready
        val trackView: TrackingView = mock { on { isTrackLineReady() }.then { true } }
        val trackingPresenter = TrackingPresenter(trackView, mock{ })

        trackingPresenter.onNewLocation(list)

        verify(trackView, times(1)).updateTrackLine(list)
        verify(trackView, never()).prepareMapToTrack(any())
    }

    @Test
    fun onNewLocation_whenTrackLineIsReadyAndMapIsLocked_shouldMoveTrackingViewCameraOnly() {

        val trackView: TrackingView = mock { on { isTrackLineReady() }.then { true } }
        val trackingPresenter = TrackingPresenter(trackView, mock { })
        // Map is locked by default

        trackingPresenter.onNewLocation(list)

        verify(trackView, times(1)).moveMapCamera(list.last())

        reset(trackView)

        // disables map
        trackingPresenter.onClickLockMap()
        trackingPresenter.onNewLocation(list)

        verify(trackingView, never()).moveMapCamera(any())
    }

    @Test
    fun onLocationAvailability_whenLocationIsNotAvailable_shouldShowInfo() {

        val trackingPresenter =
                TrackingPresenter(trackingView, mock { on { isServiceInProgress() }.then { true } })

        trackingPresenter.onLocationAvailability(false)

        verify(trackingView, never()).isInfoWaitForLocationVisible = false
        verify(trackingView, times(1)).isInfoWaitForLocationVisible = true
        verify(trackingView, never()).postMessage(any())

        reset(trackingView)

        trackingPresenter.onLocationAvailability(true)

        verify(trackingView, times(1)).isInfoWaitForLocationVisible = false
        verify(trackingView, never()).isInfoWaitForLocationVisible = true
        verify(trackingView, never()).postMessage(any())

    }

    @Test
    fun onLocationAvailability_whenLocationIsNotAvailableAndServiceIsNotInProgress_shouldShowMessageAndInfo() {

        val serviceInteractor: TrackingServiceInteractor = mock {  on { isServiceInProgress() }.then { false } }
        val trackingPresenter = TrackingPresenter(trackingView, serviceInteractor)

        trackingPresenter.onLocationAvailability(false)

        verify(trackingView, times(1)).postMessage(Message.LOCATION_NOT_AVAILABLE)
    }

    @Test
    fun onStatsUpdate_shouldUpdateStatsOnTrackingView() {

        TrackingPresenter(trackingView, mock {  }).onStatsUpdate(map)
        verify(trackingView, times(1)).updateStats(map)
    }

    @Test
    fun notifyOnDestroy_shouldUnregisterListeners() {

        val serviceInteractor: TrackingServiceInteractor = mock {  }
        val trackingPresenter = TrackingPresenter(trackingView, serviceInteractor)

        trackingPresenter.notifyOnDestroy(any())

        verify(serviceInteractor, times(1)).removeOnStatsUpdateListener(trackingPresenter)
        verify(serviceInteractor, times(1)).disconnectServiceDataReceiver(trackingPresenter)
    }
}