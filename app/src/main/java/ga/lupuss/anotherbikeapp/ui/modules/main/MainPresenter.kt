package ga.lupuss.anotherbikeapp.ui.modules.main

import ga.lupuss.anotherbikeapp.Message
import ga.lupuss.anotherbikeapp.base.Presenter
import ga.lupuss.anotherbikeapp.models.base.*
import ga.lupuss.anotherbikeapp.models.firebase.OnDocumentChanged
import ga.lupuss.anotherbikeapp.models.dataclass.Statistic
import ga.lupuss.anotherbikeapp.ui.modules.tracking.TrackingPresenter
import timber.log.Timber

/**
 * Presenter associated with [MainActivity]. [MainActivity] must implement [view].
 */
class MainPresenter(private val routesManager: RoutesManager,
                    private val authInteractor: AuthInteractor,
                    private val preferencesInteractor: PreferencesInteractor,
                    private val trackingServiceGovernor: TrackingServiceGovernor,
                    mainView: MainView)
    :   Presenter<MainView>(),
        OnDocumentChanged,
        TrackingServiceGovernor.OnServiceActivityChangesListener,
        PreferencesInteractor.OnUnitChangedListener,
        RoutesManager.OnRequestMoreShortRouteDataListener,
        TrackingServiceGovernor.OnTrackingRequestDone {

    init {
        this.view = mainView
    }

    var speedUnit: Statistic.Unit = preferencesInteractor.speedUnit
    var distanceUnit: Statistic.Unit = preferencesInteractor.distanceUnit

    private var loadMoreAvailable = true

    override fun notifyOnViewReady() {
        super.notifyOnViewReady()

        view.setTrackingButtonState(trackingServiceGovernor.isServiceActive)
        view.isNoDataTextVisible = false
        view.setDrawerHeaderInfo(authInteractor.getDisplayName(), authInteractor.getEmail())

        preferencesInteractor.addOnUnitChangedListener(this, this)
        trackingServiceGovernor.addOnServiceActivityChangesListener(this, this)
        routesManager.addRoutesDataChangedListener(this)

        onLoadMoreRequest()
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

                        routesManager.keepTempRoute(it)
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
        routesManager.removeOnRoutesDataChangedListener(this)
        preferencesInteractor.removeOnUnitChangedListener(this)
    }

    fun notifyRecyclerReachedBottom() {

        onLoadMoreRequest()
    }

    private fun onLoadMoreRequest() {

        if (loadMoreAvailable) {

            view.isProgressBarVisible = true

            routesManager.requestMoreShortRouteData(this, view)
        }
    }

    override fun onDataEnd() {

        view.isProgressBarVisible = false

        loadMoreAvailable = false

        if (routesManager.shortRouteDataCount() == 0)
            view.isNoDataTextVisible = true

    }

    override fun onFail(exception: Exception) {

        view.isProgressBarVisible = false
        Timber.e(exception)
    }

    fun onClickShortRoute(position: Int) {

        view.startSummaryActivity(
                routesManager.routeReferenceSerializer.serialize(
                        routesManager.getRouteReference(position)
                )
        )

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

    fun onHistoryRecyclerItemRequest(position: Int) = routesManager.readShortRouteData(position)

    fun onHistoryRecyclerItemCountRequest(): Int = routesManager.shortRouteDataCount()

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

    override fun onNewDocument(position: Int) {

        view.isNoDataTextVisible = false
        view.notifyRecyclerItemInserted(position, routesManager.shortRouteDataCount())
    }

    override fun onDocumentModified(position: Int) {
        view.notifyRecyclerItemChanged(position)
    }

    override fun onDocumentDeleted(position: Int) {
        view.notifyRecyclerItemRemoved(position, routesManager.shortRouteDataCount())

        if (routesManager.shortRouteDataCount() == 0) {

            view.isNoDataTextVisible = true
        }
    }

    override fun onUnitChanged(speedUnit: Statistic.Unit.Speed, distanceUnit: Statistic.Unit.Distance) {

        this.speedUnit = speedUnit
        this.distanceUnit = distanceUnit

        view.refreshRecyclerAdapter()
    }

    class Request {
        companion object {
            const val TRACKING_ACTIVITY_REQUEST = 0
            const val TRACKING_NOTIFICATION_REQUEST = 1
        }
    }
}
