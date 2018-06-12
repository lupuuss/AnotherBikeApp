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
        val points: MutableList<LatLng>
) {

    fun toImmutable() = ExtendedRouteData(
            name, distance, avgSpeed, maxSpeed, duration, startTimeStr, startTime, points
    )
}
