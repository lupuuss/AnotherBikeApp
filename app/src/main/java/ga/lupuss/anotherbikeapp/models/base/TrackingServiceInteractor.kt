package ga.lupuss.anotherbikeapp.models.base

import com.google.android.gms.maps.model.LatLng
import ga.lupuss.anotherbikeapp.models.dataclass.ExtendedRouteData
import ga.lupuss.anotherbikeapp.models.dataclass.RoutePhoto
import ga.lupuss.anotherbikeapp.models.dataclass.Statistic

interface TrackingServiceInteractor {

    interface LocationDataReceiver {

        fun onNewLocation(points: List<LatLng>)
        fun onLocationAvailability(available: Boolean)
    }

    interface OnStatsUpdateListener {
        fun onStatsUpdate(stats: Map<Statistic.Name, Statistic<*>>)
    }

    val lastLocationAvailability: Boolean

    val savedRoute: List<LatLng>

    val lastStats: Map<Statistic.Name, Statistic<*>>?

    val routeData: ExtendedRouteData

    val tempPhotos: MutableList<RoutePhoto>

    fun isServiceInProgress(): Boolean

    fun connectServiceDataReceiver(locationDataReceiver: LocationDataReceiver)

    fun disconnectServiceDataReceiver(locationDataReceiver: LocationDataReceiver)

    fun addOnStatsUpdateListener(onStatsUpdateListener: OnStatsUpdateListener)

    fun removeOnStatsUpdateListener(onStatsUpdateListener: OnStatsUpdateListener)
}