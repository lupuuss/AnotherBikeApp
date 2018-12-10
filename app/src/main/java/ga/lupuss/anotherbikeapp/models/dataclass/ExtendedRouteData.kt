package ga.lupuss.anotherbikeapp.models.dataclass

import com.google.android.gms.maps.model.LatLng

interface ExtendedRouteData : RouteData {

    val points: List<LatLng>

    class Instance(
            override val name: String?,
            override val distance: Double,
            override val avgSpeed: Double,
            override val maxSpeed: Double,
            override val duration: Long,
            override val startTimeStr: String,
            override val startTime: Long,
            override val avgAltitude: Double,
            override val maxAltitude: Double,
            override val minAltitude: Double,
            override val photos: List<RoutePhoto>,
            override val points: List<LatLng>
    ) : ExtendedRouteData


    fun toMutable() = MutableExtendedRouteData.Instance(
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
            photos = photos.toMutableList(),
            points = points.toMutableList()
    )
}
