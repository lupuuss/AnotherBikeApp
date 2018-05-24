package ga.lupuss.anotherbikeapp.models.pojo

import com.google.android.gms.maps.model.LatLng

class ExtendedRouteData(
        name: String?,
        distance: Double,
        avgSpeed: Double,
        maxSpeed: Double,
        duration: Long,
        startTimeStr: String,
        startTime: Long,
        val points: MutableList<LatLng>
) : RouteData(name, distance, avgSpeed, maxSpeed, duration, startTimeStr, startTime)
