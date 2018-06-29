package ga.lupuss.anotherbikeapp.ui.modules.main

import com.nhaarman.mockito_kotlin.*
import ga.lupuss.anotherbikeapp.Message
import ga.lupuss.anotherbikeapp.models.base.*
import ga.lupuss.anotherbikeapp.models.dataclass.ExtendedRouteData
import ga.lupuss.anotherbikeapp.models.dataclass.Statistic
import ga.lupuss.anotherbikeapp.ui.modules.tracking.TrackingPresenter
import org.junit.Test
import java.lang.Exception
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

class MainPresenterTest {

    private val routeData = mock<ExtendedRouteData> { }
    private val mainView: MainView = mock {
        on { showExitWarningDialog(any()) }
                .then { (it.getArgument(0) as (() -> Unit)?)?.invoke() }
    }
    private val routesManager: RoutesManager = mock { }
    private val authInteractor: AuthInteractor = mock { }
    private val preferencesInteractor: PreferencesInteractor = mock { }
    private val trackingServiceGovernor: TrackingServiceGovernor = mock {
        on { serviceInteractor }.then {
            mock<TrackingServiceInteractor> {
                on { it.routeData }.then { routeData }
                on { it.isServiceInProgress() }.then { false }
            }
        }
    }
    private val mainPresenter: MainPresenter = MainPresenter(
            routesManager,
            authInteractor,
            preferencesInteractor,
            trackingServiceGovernor,
            mainView
    )

    @Test
    fun notifyOnViewReady_shouldPrepareViewRegisterListenersAndInitDocumentsLoading() {
        mainPresenter.notifyOnViewReady()

        // preparing view
        verify(mainView, times(1)).setTrackingButtonState(any())
        verify(mainView, times(1)).isNoDataTextVisible = false
        verify(mainView, times(1)).setDrawerHeaderInfos(null, null)

        // registering listeners
        verify(preferencesInteractor, times(1))
                .addOnUnitChangedListener(mainPresenter, mainPresenter)
        verify(trackingServiceGovernor, times(1))
                .addOnServiceActivityChangesListener(any(), any())
        verify(routesManager, times(1))
                .addRoutesDataChangedListener(any())

        // loading
        verify(mainView, times(1)).isProgressBarVisible = true
        verify(routesManager, times(1)).requestMoreShortRouteData(any(), any())
    }

    @Test
    fun notifyOnResult_shouldStopTracking_whenResultCodeEqualsDone() {

        mainPresenter.notifyOnResult(
                MainPresenter.Request.TRACKING_ACTIVITY_REQUEST,
                TrackingPresenter.Result.DONE
        )

        verify(trackingServiceGovernor, times(1)).stopTracking()
        verify(routesManager, times(1)).keepTempRoute(routeData)
        verify(mainView, times(1)).startSummaryActivity()

    }

    @Test
    fun notifyOnResult_shouldNotStopTracking_whenResultCodeEqualsNoData() {

        mainPresenter.notifyOnResult(
                MainPresenter.Request.TRACKING_ACTIVITY_REQUEST,
                TrackingPresenter.Result.NO_DATA_DONE
        )

        verify(trackingServiceGovernor, times(1)).stopTracking()
        verify(mainView, never()).startSummaryActivity()
    }

    @Test
    fun notifyOnResult_shouldStopTracking_whenResultCodeEqualsNotDone() {
        mainPresenter.notifyOnResult(
                MainPresenter.Request.TRACKING_ACTIVITY_REQUEST,
                TrackingPresenter.Result.NOT_DONE
        )

        verify(trackingServiceGovernor, never()).stopTracking()
        verify(mainView, never()).startSummaryActivity()
    }

    @Test
    fun notifyOnDestroy_shouldUnregisterListeners() {

        mainPresenter.notifyOnDestroy(any())

        verify(trackingServiceGovernor, times(1)).removeOnServiceActivityChangesListener(mainPresenter)
        verify(preferencesInteractor, times(1)).removeOnUnitChangedListener(mainPresenter)
        verify(routesManager, times(1)).removeOnRoutesDataChangedListener(mainPresenter)
    }

    @Test
    fun notifyRecyclerReachedBottom_whenItIsAvailable_shouldLoadMoreShortRouteData() {

        setLoadMoreAvailable(mainPresenter, true)

        mainPresenter.notifyRecyclerReachedBottom()

        verify(mainView, times(1)).isProgressBarVisible = true
        verify(routesManager, times(1)).requestMoreShortRouteData(mainPresenter, mainView)

        reset(mainView)
        reset(routesManager)

        setLoadMoreAvailable(mainPresenter, false)

        mainPresenter.notifyRecyclerReachedBottom()

        verify(routesManager, never()).requestMoreShortRouteData(any(), any())
        verify(mainView, never()).isProgressBarVisible = true
    }

    private fun setLoadMoreAvailable(mainPresenter: MainPresenter, available: Boolean) {

        (mainPresenter::class.memberProperties
                .find { it.name == "loadMoreAvailable" }
                ?.apply { isAccessible = true }
                as KMutableProperty<*>)
                .setter
                .call(mainPresenter, available)
    }

    private fun getLoadMoreAvailable(mainPresenter: MainPresenter): Boolean {

        return mainPresenter::class.memberProperties
                .find { it.name == "loadMoreAvailable" }
                ?.apply { isAccessible = true }
                ?.getter
                ?.call(mainPresenter)
                as Boolean
    }

    @Test
    fun onDataEnd_shouldDisableProgressBarAndAvoidLoadingMoreData() {

        mainPresenter.onDataEnd()

        verify(mainView, times(1)).isProgressBarVisible = false
        assert(!getLoadMoreAvailable(mainPresenter))
    }

    @Test
    fun onDataEnd_whenThereIsNoData_shouldShowNoDataText() {

        val mainPresenter = MainPresenter(
                mock { on { shortRouteDataCount() }.then { 0 } },
                authInteractor,
                preferencesInteractor,
                trackingServiceGovernor,
                mainView
        )

        mainPresenter.onDataEnd()

        verify(mainView, times(1)).isNoDataTextVisible = true
    }

    @Test
    fun onFail_shouldDisableProgressBar() {

        mainPresenter.onFail(Exception())

        verify(mainView, times(1)).isProgressBarVisible = false
    }

    @Test
    fun onClickShortRoute_shouldStartSummaryActivity() {
        val mainPresenter = MainPresenter(
                mock {
                    on { routeReferenceSerializer }.then { mock<RouteReferenceSerializer> {
                        on { serialize(any()) }.then { "" }
                    } }

                    on { getRouteReference(0) }.then { mock<RouteReference>{ } }
                },
                authInteractor,
                preferencesInteractor,
                trackingServiceGovernor,
                mainView
        )
        mainPresenter.onClickShortRoute(0)
        verify(mainView, times(1)).startSummaryActivity(eq(""))
    }

    @Test
    fun onClickTrackingButton_shouldStartTracking() {

        mainPresenter.onClickTrackingButton()

        verify(trackingServiceGovernor, times(1)).startTracking(mainPresenter)
    }

    @Test
    fun onTrackingInitDone_shouldStartTrackingActivity() {

        mainPresenter.onTrackingRequestDone()

        verify(mainView, times(1)).startTrackingActivity()
    }

    @Test
    fun onNoTrackingPermission_shouldShowNoPermissionMessage() {

        mainPresenter.onTrackingRequestNoPermission()

        verify(mainView, times(1)).postMessage(Message.NO_PERMISSION)
    }

    @Test
    fun onClickSettings_shouldStartSettingsActivity() {
        mainPresenter.onClickSettings()

        verify(mainView, times(1)).startSettingsActivity()
    }

    @Test
    fun onClickSignOut_whenTrackingNotInProgress_shouldSignOutAndStartLoginActivity() {

        mainPresenter.onClickSignOut()

        verify(mainView, never()).showExitWarningDialog(any())
        verify(authInteractor, times(1)).signOut()
        verify(mainView, times(1)).startLoginActivity()
        verify(mainView, times(1)).finishActivity()
    }

    @Test
    fun onClickSignOut_whenTrackingInProgress_shouldShowExitWarningDialog() {

        val mainPresenter = MainPresenter(
                routesManager,
                authInteractor,
                preferencesInteractor,
                mock {
                    on { serviceInteractor }.then {
                        mock<TrackingServiceInteractor> {
                            on { isServiceInProgress() }.then { true }
                        }
                    }
                },
                mainView
        )

        mainPresenter.onClickSignOut()

        verify(mainView, times(1)).showExitWarningDialog(any())
        verify(mainView, times(1)).startLoginActivity()
        verify(mainView, times(1)).finishActivity()
        verify(authInteractor, times(1)).signOut()
    }

    @Test
    fun onHistoryRecyclerItemRequest_shouldDelegateToRoutesManager() {
        mainPresenter.onHistoryRecyclerItemRequest(any())
        verify(routesManager, times(1)).readShortRouteData(any())
    }

    @Test
    fun onHistoryRecyclerItemCountRequest_shouldDelegateToRoutesManager() {
        mainPresenter.onHistoryRecyclerItemCountRequest()
        verify(routesManager, times(1)).shortRouteDataCount()
    }

    @Test
    fun onExitRequest_whenDrawerOpened_shouldHideDrawer() {
        val mainView = mock<MainView> {
            on { isDrawerLayoutOpened }.then { true }
        }
        val mainPresenter = MainPresenter(
                routesManager,
                authInteractor,
                preferencesInteractor,
                trackingServiceGovernor,
                mainView
        )

        mainPresenter.onExitRequest()

        verify(mainView, times(1)).hideDrawer()
        verify(mainView, times(1)).isDrawerLayoutOpened
    }

    @Test
    fun onExitRequest_whenTrackingNotInProgress_shouldFinishActivity() {

        mainPresenter.onExitRequest()

        verify(mainView, times(1)).finishActivity()
        verify(mainView, never()).showExitWarningDialog(any())
    }

    @Test
    fun onExitRequest_whenTrackingInProgress_shouldShowExitWarningDialog() {
        val mainPresenter = MainPresenter(
                routesManager,
                authInteractor,
                preferencesInteractor,
                mock {
                    on { serviceInteractor }.then {
                        mock<TrackingServiceInteractor> {
                            on { isServiceInProgress() }.then { true }
                        }
                    }
                },
                mainView
        )

        mainPresenter.onExitRequest()
        verify(mainView, times(1)).showExitWarningDialog(any())
        verify(mainView, times(1)).finishActivity()
    }

    @Test
    fun onNewDocument_shouldHideNoDataTextAndNotifyRecycler() {

        mainPresenter.onNewDocument(1)

        verify(mainView, times(1)).isNoDataTextVisible = false
        verify(mainView, times(1)).notifyRecyclerItemInserted(eq(1), any())
    }

    @Test
    fun onDocumentModified_shouldNotifyRecycler() {

        mainPresenter.onDocumentModified(1)

        verify(mainView, times(1)).notifyRecyclerItemChanged(eq(1))
    }

    @Test
    fun onDocumentDeleted_shouldNotifyRecycler() {

        mainPresenter.onDocumentDeleted(1)

        verify(mainView, times(1)).notifyRecyclerItemRemoved(eq(1), any())
    }

    @Test
    fun onDocumentDeleted_whenThereIsNoData_shouldShowNoDataText() {
        val mainPresenter = MainPresenter(
                mock { on { shortRouteDataCount() }.then { 0 } },
                authInteractor,
                preferencesInteractor,
                trackingServiceGovernor,
                mainView
        )

        mainPresenter.onDocumentDeleted(1)

        verify(mainView, times(1)).isNoDataTextVisible = true
    }

    @Test
    fun onServiceActivityChanged_shouldChangeTrackingButtonState() {

        mainPresenter.onServiceActivityChanged(true)
        verify(mainView, times(1)).setTrackingButtonState(eq(true))

        reset(mainView)

        mainPresenter.onServiceActivityChanged(false)
        verify(mainView, times(1)).setTrackingButtonState(false)
    }

    @Test
    fun onUnitChanged_changeUnitsAndRefreshRecycler() {

        mainPresenter.onUnitChanged(Statistic.Unit.KM_H, Statistic.Unit.KM)

        assert(mainPresenter.speedUnit == Statistic.Unit.KM_H)
        assert(mainPresenter.distanceUnit == Statistic.Unit.KM)
        verify(mainView, times(1)).refreshRecyclerAdapter()
    }

}