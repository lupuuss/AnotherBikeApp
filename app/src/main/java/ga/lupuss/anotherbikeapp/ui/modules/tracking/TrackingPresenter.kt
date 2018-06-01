package ga.lupuss.anotherbikeapp.ui.modules.tracking

import android.view.View
import com.google.android.gms.maps.model.LatLng
import ga.lupuss.anotherbikeapp.Message
import ga.lupuss.anotherbikeapp.base.Presenter
import ga.lupuss.anotherbikeapp.models.trackingservice.TrackingService
import ga.lupuss.anotherbikeapp.models.pojo.Statistic
import javax.inject.Inject

/** Presenter associated with [TrackingActivity].
 * [TrackingActivity] must implement [TrackingView] */
class TrackingPresenter @Inject constructor()
    : TrackingService.LocationDataReceiver, TrackingService.OnStatsUpdateListener, Presenter {

    @Inject
    lateinit var trackingView: TrackingView

    @Inject
    lateinit var serviceBinder: TrackingService.ServiceBinder

    private var followMyLocation: Boolean = true
    private var isLocationAvailable: Boolean = false
        private set(value) {

            trackingView.setInfoWaitForLocationVisibility(

                    if (value) View.INVISIBLE else View.VISIBLE
            )
            field = value
        }

    override fun notifyOnViewReady() {

        // checking if service worked before activity start
        if (serviceBinder.savedRoute.isNotEmpty()) {

            trackingView.prepareMapToTrack(serviceBinder.savedRoute)
        }

        serviceBinder.lastStats?.let {

            trackingView.updateStats(it)
        }

        isLocationAvailable = serviceBinder.lastLocationAvailability

        // request receiving location and statistics from TrackService
        serviceBinder.connectServiceDataReceiver(this)
        serviceBinder.addOnStatsUpdateListener(this)
    }

    fun onMyLocationButtonClick() {

        followMyLocation = true

        if (serviceBinder.savedRoute.isNotEmpty()) {

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

        isLocationAvailable = available

        if (!available && !serviceBinder.isServiceInProgress()) {

            trackingView.postMessage(Message.LOCATION_NOT_AVAILABLE)
        }
    }

    override fun onStatsUpdate(stats: Map<Statistic.Name, Statistic<*>>) {

        trackingView.updateStats(stats)
    }

    override fun notifyOnDestroy(isFinishing: Boolean) {

        serviceBinder.removeOnStatsUpdateListener(this)
        serviceBinder.disconnectServiceDataReceiver(this)
    }
}