package ga.lupuss.anotherbikeapp.ui.modules.tracking

import com.google.android.gms.maps.model.LatLng
import ga.lupuss.anotherbikeapp.Message
import ga.lupuss.anotherbikeapp.base.Presenter
import ga.lupuss.anotherbikeapp.models.base.LocalPhotosManager
import ga.lupuss.anotherbikeapp.models.base.TrackingServiceInteractor

import ga.lupuss.anotherbikeapp.models.dataclass.RoutePhoto
import ga.lupuss.anotherbikeapp.models.dataclass.Statistic
import javax.inject.Inject

/** Presenter associated with [TrackingActivity].
 * [TrackingActivity] must implement [TrackingView] */
class TrackingPresenter @Inject constructor(
        trackingView: TrackingView,
        private val serviceInteractor: TrackingServiceInteractor,
        private val localPhotosManager: LocalPhotosManager
) : TrackingServiceInteractor.LocationDataReceiver, TrackingServiceInteractor.OnStatsUpdateListener, Presenter<TrackingView>() {

    init {
        view = trackingView
    }

    private var followMyLocation: Boolean = true
        set(value) {

            view.isMapButtonInLockState = value
            field = value
        }

    private var isLocationAvailable: Boolean = false
        private set(value) {

            view.isInfoWaitForLocationVisible = !value
            field = value
        }

    val localPhotosSizeCallback = { localPhotosManager.photosCount }
    val getLocalPhotoCallback = {
        position: Int -> localPhotosManager.getLocalPhoto(position)
    }

    override fun notifyOnViewReady() {

        // checking if service worked before activity start
        if (serviceInteractor.savedRoute.isNotEmpty()) {

            view.prepareMapToTrack(serviceInteractor.savedRoute)
        }

        serviceInteractor.lastStats?.let {

            view.updateStats(it)
        }

        followMyLocation = view.isMapButtonInLockState

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

                view.moveMapCamera(serviceInteractor.savedRoute.last())
            }
        }
    }

    fun onClickFinishTracking() {

        if (serviceInteractor.isServiceInProgress()) {

            view.showFinishTrackingDialog {

                view.finishActivityWithResult(Result.DONE)
            }

        } else {

            view.finishActivityWithResult(Result.NO_DATA_DONE)
        }
    }

    override fun onNewLocation(points: List<LatLng>) {

        if (!view.isTrackLineReady()) {

            view.prepareMapToTrack(points)

        } else {

            view.updateTrackLine(points)

            if (followMyLocation) {

                view.moveMapCamera(points.last())
            }
        }

    }

    override fun onLocationAvailability(available: Boolean) {

        isLocationAvailable = available

        if (!available && !serviceInteractor.isServiceInProgress()) {

            view.postMessage(Message.LOCATION_NOT_AVAILABLE)
        }
    }

    override fun onStatsUpdate(stats: Map<Statistic.Name, Statistic<*>>) {

        view.updateStats(stats)
    }

    override fun notifyOnDestroy(isFinishing: Boolean) {

        serviceInteractor.removeOnStatsUpdateListener(this)
        serviceInteractor.disconnectServiceDataReceiver(this)
    }

    fun notifyOnNewPhoto(photo: RoutePhoto) {

        localPhotosManager.addLocalPhoto(photo)
        view.notifyNewPhoto(0, localPhotosSizeCallback())
    }

    /** Possible results codes */
    class Result {

        companion object {
            const val NOT_DONE = 0
            const val NO_DATA_DONE = 1
            const val DONE = 2
        }
    }
}