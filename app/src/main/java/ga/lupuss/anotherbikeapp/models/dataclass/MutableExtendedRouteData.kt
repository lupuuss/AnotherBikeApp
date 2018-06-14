package ga.lupuss.anotherbikeapp.models.dataclass

import com.google.android.gms.maps.model.LatLng

class MutableExtendedRouteData(
        var name: String?,
        var distance: Double,
        var avgSpeed: Double,
        var maxSpeed: Double,
        var duration: Long,
        var startTimeStr: String,
        var startTime: Long,
        var avgAltitude: Double,
        var maxAltitude: Double,
        var minAltitude: Double,
        val points: MutableList<LatLng>
) {

    fun toImmutable() = ExtendedRouteData(
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
            points = points.toList()
    )
}
