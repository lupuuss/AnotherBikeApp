package ga.lupuss.anotherbikeapp.ui.modules.main

import android.view.View
import ga.lupuss.anotherbikeapp.Message
import ga.lupuss.anotherbikeapp.base.Presenter
import ga.lupuss.anotherbikeapp.models.interfaces.AuthInteractor
import ga.lupuss.anotherbikeapp.models.firebase.OnDocumentChanged
import ga.lupuss.anotherbikeapp.models.interfaces.PreferencesInteractor
import ga.lupuss.anotherbikeapp.models.interfaces.RoutesManager
import ga.lupuss.anotherbikeapp.models.interfaces.TrackingServiceGovernor
import ga.lupuss.anotherbikeapp.models.dataclass.Statistic
import ga.lupuss.anotherbikeapp.ui.modules.summary.SummaryPresenter
import ga.lupuss.anotherbikeapp.ui.modules.tracking.TrackingActivity
import timber.log.Timber

import javax.inject.Inject
/**
 * Presenter associated with [MainActivity]. [MainActivity] must implement [MainView].
 */
class MainPresenter @Inject constructor(private val routesManager: RoutesManager,
                                        private val authInteractor: AuthInteractor,
                                        private val preferencesInteractor: PreferencesInteractor,
                                        private val trackingServiceGovernor: TrackingServiceGovernor)
    :   Presenter,
        OnDocumentChanged,
        TrackingServiceGovernor.OnServiceActivityChangesListener,
        PreferencesInteractor.OnUnitChangedListener {

    var speedUnit: Statistic.Unit = preferencesInteractor.speedUnit
    var distanceUnit: Statistic.Unit = preferencesInteractor.distanceUnit

    @Inject
    lateinit var mainView: MainView

    override fun notifyOnViewReady() {
        super.notifyOnViewReady()

        mainView.setTrackingButtonState(trackingServiceGovernor.isServiceActive)
        mainView.setNoDataTextVisibility(View.INVISIBLE)
        mainView.setDrawerHeaderInfos(authInteractor.getDisplayName(), authInteractor.getEmail())

        preferencesInteractor.addOnUnitChangedListener(this, this)
        trackingServiceGovernor.addOnServiceActivityChangesListener(this, this)
        routesManager.addRoutesDataChangedListener(this)

        onLoadMoreRequest()

    }

    override fun notifyOnResult(requestCode: Int, resultCode: Int) {

        if (requestCode == MainActivity.Request.TRACKING_ACTIVITY_REQUEST) {

            when (resultCode) {

                TrackingActivity.Result.DONE -> {

                    Timber.d("Service done. Data may be saved")

                    val routeData = trackingServiceGovernor.serviceInteractor?.routeData

                    trackingServiceGovernor.stopTracking()

                    routeData?.let {

                        routesManager.keepTempRoute(it)
                        mainView.startSummaryActivity()
                    }
                }

                TrackingActivity.Result.NO_DATA_DONE -> {

                    Timber.d("Service done, but no data")
                    trackingServiceGovernor.stopTracking()
                }

                TrackingActivity.Result.NOT_DONE -> {

                    Timber.d("Service is working...")
                }
            }
        }
    }

    override fun notifyOnDestroy(isFinishing: Boolean) {

        trackingServiceGovernor.removeOnServiceActivityChangesListener(this)
        trackingServiceGovernor.destroy(isFinishing)
        routesManager.removeOnRoutesDataChangedListener(this)
    }

    fun notifyRecyclerReachedBottom() {

        onLoadMoreRequest()
    }

    private fun onLoadMoreRequest() {
        mainView.setProgressBarVisibility(View.VISIBLE)

        routesManager.requestMoreShortRouteData({

            mainView.setProgressBarVisibility(View.GONE)

            if (routesManager.shortRouteDataCount() == 0)
                mainView.setNoDataTextVisibility(View.VISIBLE)

        }, {
            mainView.setProgressBarVisibility(View.GONE)
            Timber.d(it)
        })
    }

    fun onClickShortRoute(position: Int) {

        mainView.startSummaryActivity(routesManager.getMoreInfoReference(position))

    }

    fun onClickTrackingButton() {

        trackingServiceGovernor.startTracking(
                { mainView.startTrackingActivity() },
                { mainView.postMessage(Message.NO_PERMISSION) }
        )

    }

    fun onClickSettings() {

        mainView.startSettingsActivity()
    }

    fun onClickSignOut() {

        if (trackingServiceGovernor.serviceInteractor == null
                || !trackingServiceGovernor.serviceInteractor!!.isServiceInProgress()) {

            signOut()
        } else {
            mainView.showExitWarningDialog({ signOut() })
        }
    }

    private fun signOut() {
        authInteractor.signOut()
        mainView.startLoginActivity()
        mainView.finishActivity()
    }

    fun onHistoryRecyclerItemRequest(position: Int) = routesManager.readShortRouteData(position)

    fun onHistoryRecyclerItemCountRequest(): Int = routesManager.shortRouteDataCount()

    fun onExitRequest() {

        if (trackingServiceGovernor.serviceInteractor == null
                || !trackingServiceGovernor.serviceInteractor!!.isServiceInProgress()) {

            mainView.finishActivity()

        } else {

            mainView.showExitWarningDialog({ mainView.finishActivity() })
        }
    }

    override fun onNewDocument(position: Int) {

        mainView.setNoDataTextVisibility(View.INVISIBLE)
        mainView.notifyRecyclerItemInserted(position)
    }

    override fun onDocumentModified(position: Int) {
        mainView.notifyRecyclerItemChanged(position)
    }

    override fun onDocumentDeleted(position: Int) {
        mainView.notifyRecyclerItemRemoved(position)

        if (routesManager.shortRouteDataCount() == 0) {

            mainView.setNoDataTextVisibility(View.VISIBLE)
        }
    }

    override fun onServiceActivityChanged(state: Boolean) {

        mainView.setTrackingButtonState(state)
    }

    override fun onUnitChanged(speedUnit: Statistic.Unit, distanceUnit: Statistic.Unit) {

        this.speedUnit = speedUnit
        this.distanceUnit = distanceUnit

        mainView.refreshRecyclerAdapter()
    }
}
