package ga.lupuss.anotherbikeapp.models.interfaces

import com.google.android.gms.maps.model.LatLng
import ga.lupuss.anotherbikeapp.models.pojo.ExtendedRouteData
import ga.lupuss.anotherbikeapp.models.trackingservice.TrackingService
import ga.lupuss.anotherbikeapp.models.pojo.Statistic

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

    fun isServiceInProgress(): Boolean

    fun connectServiceDataReceiver(locationDataReceiver: LocationDataReceiver)

    fun disconnectServiceDataReceiver(locationDataReceiver: LocationDataReceiver)

    fun addOnStatsUpdateListener(onStatsUpdateListener: OnStatsUpdateListener)

    fun removeOnStatsUpdateListener(onStatsUpdateListener: OnStatsUpdateListener)
}