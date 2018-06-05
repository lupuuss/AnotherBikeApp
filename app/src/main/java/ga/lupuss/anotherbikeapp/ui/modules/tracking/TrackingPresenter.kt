package ga.lupuss.anotherbikeapp.ui.modules.tracking

import com.google.android.gms.maps.model.LatLng
import ga.lupuss.anotherbikeapp.Message
import ga.lupuss.anotherbikeapp.base.Presenter
import ga.lupuss.anotherbikeapp.models.base.TrackingServiceInteractor
import ga.lupuss.anotherbikeapp.models.dataclass.Statistic
import javax.inject.Inject

/** Presenter associated with [TrackingActivity].
 * [TrackingActivity] must implement [TrackingView] */
class TrackingPresenter @Inject constructor(
        private val trackingView: TrackingView,
        private val serviceInteractor: TrackingServiceInteractor
) : TrackingServiceInteractor.LocationDataReceiver, TrackingServiceInteractor.OnStatsUpdateListener, Presenter {

    private var followMyLocation: Boolean = true
        set(value) {

            trackingView.mapLockButtonState = value
            field = value
        }

    private var isLocationAvailable: Boolean = false
        private set(value) {

            trackingView.isInfoWaitForLocationVisible = !value
            field = value
        }

    override fun notifyOnViewReady() {

        // checking if service worked before activity start
        if (serviceInteractor.savedRoute.isNotEmpty()) {

            trackingView.prepareMapToTrack(serviceInteractor.savedRoute)
        }

        serviceInteractor.lastStats?.let {

            trackingView.updateStats(it)
        }

        followMyLocation = trackingView.mapLockButtonState

        isLocationAvailable = serviceInteractor.lastLocationAvailability

        // request receiving location and statistics from TrackService
        serviceInteractor.connectServiceDataReceiver(this)
        serviceInteractor.addOnStatsUpdateListener(this)
    }

    fun onClickLockMap() {

        if (followMyLocation) {

            followMyLocation = false

        } else {

            followMyLocation = true

            if (serviceInteractor.savedRoute.isNotEmpty()) {

                trackingView.moveMapCamera(serviceInteractor.savedRoute.last())
            }
        }
    }

    fun onClickFinishTracking() {

        if (serviceInteractor.isServiceInProgress()) {

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

        if (!available && !serviceInteractor.isServiceInProgress()) {

            trackingView.postMessage(Message.LOCATION_NOT_AVAILABLE)
        }
    }

    override fun onStatsUpdate(stats: Map<Statistic.Name, Statistic<*>>) {

        trackingView.updateStats(stats)
    }

    override fun notifyOnDestroy(isFinishing: Boolean) {

        serviceInteractor.removeOnStatsUpdateListener(this)
        serviceInteractor.disconnectServiceDataReceiver(this)
    }
}