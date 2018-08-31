package ga.lupuss.anotherbikeapp.ui.modules.main

import ga.lupuss.anotherbikeapp.Message
import ga.lupuss.anotherbikeapp.base.Presenter
import ga.lupuss.anotherbikeapp.models.base.*
import ga.lupuss.anotherbikeapp.ui.modules.tracking.TrackingPresenter
import timber.log.Timber

/**
 * Presenter associated with [MainActivity]. [MainActivity] must implement [view].
 */
class MainPresenter(private val routesManager: RoutesManager,
                    private val authInteractor: AuthInteractor,
                    private val trackingServiceGovernor: TrackingServiceGovernor,
                    mainView: MainView)
    :   Presenter<MainView>(),
        TrackingServiceGovernor.OnServiceActivityChangesListener,
        TrackingServiceGovernor.OnTrackingRequestDone {

    init {
        this.view = mainView
    }

    override fun notifyOnViewReady() {
        super.notifyOnViewReady()

        view.setTrackingButtonState(trackingServiceGovernor.isServiceActive)
        view.setDrawerHeaderInfo(authInteractor.getDisplayName(), authInteractor.getEmail())

        trackingServiceGovernor.addOnServiceActivityChangesListener(this, this)
    }

    override fun notifyOnResult(requestCode: Int, resultCode: Int) {

        if(requestCode == Request.TRACKING_NOTIFICATION_REQUEST) {

            trackingServiceGovernor.startTracking(this)

        } else if (requestCode == Request.TRACKING_ACTIVITY_REQUEST) {

            when (resultCode) {

                TrackingPresenter.Result.DONE -> {

                    Timber.v("Service done. Data may be saved")

                    val routeData = trackingServiceGovernor.serviceInteractor?.routeData

                    trackingServiceGovernor.stopTracking()

                    routeData?.let {

                        routesManager.keepTempRoute(RoutesManager.Slot.MAIN_TO_SUMMARY, it)
                        view.startSummaryActivity()
                    }
                }

                TrackingPresenter.Result.NO_DATA_DONE -> {

                    Timber.v("Service done, but no data")
                    trackingServiceGovernor.stopTracking()
                }

                TrackingPresenter.Result.NOT_DONE -> {

                    Timber.v("Service is working...")
                }
            }

        }
    }

    override fun notifyOnDestroy(isFinishing: Boolean) {

        super.notifyOnDestroy(isFinishing)
        trackingServiceGovernor.removeOnServiceActivityChangesListener(this)
    }

    fun onClickTrackingButton() {

        trackingServiceGovernor.startTracking(this)
    }

    override fun onTrackingRequestDone() {

        view.startTrackingActivity()
    }

    override fun onTrackingRequestNoPermission() {

        view.postMessage(Message.NO_PERMISSION)
    }

    override fun onServiceActivityChanged(state: Boolean) {

        view.setTrackingButtonState(state)
    }

    fun onClickSettings() {

        view.startSettingsActivity()
    }

    fun onClickSignOut() {

        if (trackingServiceGovernor.serviceInteractor == null
                || !trackingServiceGovernor.serviceInteractor!!.isServiceInProgress()) {

            signOut()
        } else {
            view.showExitWarningDialog({ signOut() })
        }
    }

    private fun signOut() {
        authInteractor.signOut()
        view.startLoginActivity()
        view.finishActivity()
    }

    fun onExitRequest() {

        if (view.isDrawerLayoutOpened) {

            view.hideDrawer()

        } else if (trackingServiceGovernor.serviceInteractor == null
                || !trackingServiceGovernor.serviceInteractor!!.isServiceInProgress()) {

            view.finishActivity()

        } else {

            view.showExitWarningDialog({ view.finishActivity() })
        }
    }

    class Request {
        companion object {
            const val TRACKING_ACTIVITY_REQUEST = 0
            const val TRACKING_NOTIFICATION_REQUEST = 1
        }
    }
}
