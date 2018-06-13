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
        avgAltitude: Double,
        maxAltitude: Double,
        minAltitude: Double,
        val points: List<LatLng>
) : RouteData(
        name = name,
        distance = distance,
        avgSpeed = avgSpeed,
        maxSpeed = maxSpeed,
        duration = duration,
        startTimeStr = startTimeStr,
        startTime = startTime,
        avgAltitude = avgAltitude,
        maxAltitude = maxAltitude,
        minAltitude = minAltitude
) {

    fun toMutable() = MutableExtendedRouteData(
            name = name,
            distance = distance,
            avgSpeed = avgSpeed,
            maxSpeed = maxSpeed,
            duration = duration,
            startTimeStr = startTimeStr,
            startTime = startTime,
            avgAltitude = avgAltitude,
            maxAltitude = maxAltitude,
            minAltitude = minAltitude,
            points = points.toMutableList()
    )
}
