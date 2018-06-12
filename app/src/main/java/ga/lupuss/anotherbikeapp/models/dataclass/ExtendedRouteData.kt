package ga.lupuss.anotherbikeapp.models.dataclass

import com.google.android.gms.maps.model.LatLng

class ExtendedRouteData(
        name: String?,
        distance: Double,
        avgSpeed: Double,
        maxSpeed: Double,
        duration: Long,
        startTimeStr: String,
        startTime: Long,
        val points: List<LatLng>
) : RouteData(
        name = name,
        distance = distance,
        avgSpeed = avgSpeed,
        maxSpeed = maxSpeed,
        duration = duration,
        startTimeStr = startTimeStr,
        startTime = startTime
) {

    fun toMutable() = MutableExtendedRouteData(
            name, distance, avgSpeed, maxSpeed, duration, startTimeStr, startTime, points.toMutableList()
    )
}
