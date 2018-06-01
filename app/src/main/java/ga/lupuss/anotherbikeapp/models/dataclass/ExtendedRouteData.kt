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
        val points: MutableList<LatLng>
) : RouteData(
        name = name,
        distance = distance,
        avgSpeed = avgSpeed,
        maxSpeed = maxSpeed,
        duration = duration,
        startTimeStr = startTimeStr,
        startTime = startTime
)
