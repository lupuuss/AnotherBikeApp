package ga.lupuss.anotherbikeapp.ui.modules.main

import com.nhaarman.mockito_kotlin.*
import ga.lupuss.anotherbikeapp.Message
import ga.lupuss.anotherbikeapp.models.base.*
import ga.lupuss.anotherbikeapp.models.dataclass.ExtendedRouteData
import ga.lupuss.anotherbikeapp.models.dataclass.Statistic
import ga.lupuss.anotherbikeapp.models.firebase.OnDataSetChanged
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
    private val trackingServiceGovernor: TrackingServiceGovernor = mock {
        on { serviceInteractor }.then {
            mock<TrackingServiceInteractor> {
                on { it.routeData }.then { routeData }
                on { it.isServiceInProgress() }.then { false }
            }
        }

        on { provideServiceInteractor(any()) }.then {

            (it.getArgument(0) as ((TrackingServiceInteractor) -> Unit)?)?.invoke(
                mock {
                    on { it.routeData }.then { routeData }
                    on { it.isServiceInProgress() }.then { false }
                }
            )
        }
    }
    private val mainPresenter: MainPresenter = MainPresenter(
            routesManager,
            authInteractor,
            trackingServiceGovernor,
            mainView
    )


    @Test
    fun notifyOnResult_shouldStopTracking_whenResultCodeEqualsDone() {

        mainPresenter.onClickTrackingButton()

        mainPresenter.notifyOnResult(
                MainPresenter.Request.TRACKING_ACTIVITY_REQUEST,
                TrackingPresenter.Result.DONE
        )

        verify(trackingServiceGovernor, times(1)).stopTracking()
        verify(routesManager, times(1)).keepTempRoute(RoutesManager.Slot.MAIN_TO_SUMMARY, routeData)
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

        mainPresenter.notifyOnResult(MainPresenter.Request.TRACKING_ACTIVITY_REQUEST, TrackingPresenter.Result.DONE)
        mainPresenter.notifyOnDestroy(any())

        verify(trackingServiceGovernor, times(1)).removeOnServiceActivityChangesListener(mainPresenter)
        verify(trackingServiceGovernor, times(1)).removeServiceInteractorListener(any())
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
    fun onExitRequest_whenDrawerOpened_shouldHideDrawer() {
        val mainView = mock<MainView> {
            on { isDrawerLayoutOpened }.then { true }
        }
        val mainPresenter = MainPresenter(
                routesManager,
                authInteractor,
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
    fun onServiceActivityChanged_shouldChangeTrackingButtonState() {

        mainPresenter.onServiceActivityChanged(true)
        verify(mainView, times(1)).setTrackingButtonState(eq(true))

        reset(mainView)

        mainPresenter.onServiceActivityChanged(false)
        verify(mainView, times(1)).setTrackingButtonState(false)
    }

}