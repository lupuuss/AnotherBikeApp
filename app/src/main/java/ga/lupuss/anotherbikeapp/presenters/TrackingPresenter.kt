package ga.lupuss.anotherbikeapp.presenters

import android.view.View
import com.google.android.gms.maps.model.LatLng
import ga.lupuss.anotherbikeapp.R
import ga.lupuss.anotherbikeapp.trackingservice.TrackingService
import ga.lupuss.anotherbikeapp.trackingservice.statisticsmanager.Statistic
import ga.lupuss.anotherbikeapp.ui.activities.TrackingActivity

/** Presenter associated with [TrackingActivity].
 * [TrackingActivity] must implement [TrackingPresenter.IView] */
class TrackingPresenter(
        private val trackingView: IView,
        private val serviceBinder: TrackingService.ServiceBinder

) : TrackingService.LocationDataReceiver, TrackingService.OnStatsUpdateListener, Presenter {

    interface IView : Presenter.BaseView {

        fun updateStats(statistics: Map<Statistic.Name, Statistic>)
        fun updateTrackLine(points: List<LatLng>)
        fun prepareMapToTrack(points: List<LatLng>)

        fun setInfoWaitForLocationVisibility(visibility: Int)

        fun isTrackLineReady(): Boolean

        fun moveMapCamera(latLng: LatLng)

        fun showFinishTrackingDialog(onYesAction: () -> Unit)

        fun finishActivityWithResult(result: Int)
    }

    private var followMyLocation: Boolean = true
    private var isLocationAvailable: Boolean = false
        private set(value) {
            trackingView.setInfoWaitForLocationVisibility(
                    if (isLocationAvailable) View.VISIBLE else View.INVISIBLE
            )

            field = value
        }

    fun initTracking() {

        // checking if service worked before activity start
        if (serviceBinder.savedRoute.size != 0) {

            trackingView.prepareMapToTrack(serviceBinder.savedRoute)
        }
        if (serviceBinder.lastStats != null) {
            trackingView.updateStats(serviceBinder.lastStats!!)
        }
        isLocationAvailable = serviceBinder.lastLocationAvailability

        // request receiving location and statistics from TrackService
        serviceBinder.connectServiceDataReceiver(this)
        serviceBinder.addOnStatsUpdateListener(this)
    }

    fun onMyLocationButtonClick() {

        followMyLocation = true

        if (serviceBinder.savedRoute.size != 0) {
            trackingView.moveMapCamera(serviceBinder.savedRoute.last())
        }
    }

    fun onGoogleMapClick() {
        followMyLocation = false
    }

    fun onClickFinishTracking() {
        if (serviceBinder.isServiceInProgress()) {

            trackingView.showFinishTrackingDialog {
                trackingView.finishActivityWithResult(TrackingActivity.Result.DONE)
            }

        } else {

            trackingView.finishActivityWithResult(TrackingActivity.Result.NO_DATA_DONE)
        }
    }

    override fun onNewLocation(points: List<LatLng>) {

        if (!trackingView.isTrackLineReady()) {

            trackingView.prepareMapToTrack(points)

        } else {

            trackingView.updateTrackLine(points)

            if (followMyLocation) {

                trackingView.moveMapCamera(points.last())
            }
        }

    }

    override fun onLocationAvailability(available: Boolean) {

        if (!available) {

            isLocationAvailable = false

            if (!serviceBinder.isServiceInProgress()) {

                trackingView.makeToast(R.string.location_not_available)
            }

        } else {

            isLocationAvailable = true
        }
    }

    override fun onStatsUpdate(stats: Map<Statistic.Name, Statistic>) {

        trackingView.updateStats(stats)
    }

    override fun notifyOnDestroy(isFinishing: Boolean) {

        serviceBinder.removeOnStatsUpdateListener(this)
        serviceBinder.disconnectServiceDataReceiver(this)
    }
}