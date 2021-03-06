package ga.lupuss.anotherbikeapp.models.dataclass

import com.google.android.gms.maps.model.LatLng
import ga.lupuss.anotherbikeapp.AppUnit

interface RouteData : ShortRouteData {

    val startTimeStr: String
    val maxSpeed: Double
    val avgAltitude: Double
    val maxAltitude: Double
    val minAltitude: Double

    class Instance(
            override val name: String?,
            override val distance: Double,
            override val avgSpeed: Double,
            override val duration: Long,
            override val startTime: Long,
            override val startTimeStr: String,
            override val maxSpeed: Double,
            override val avgAltitude: Double,
            override val maxAltitude: Double,
            override val minAltitude: Double
    ) : RouteData

    fun getStatisticsMap(speedUnit: AppUnit, distanceUnit: AppUnit)
            : Map<Statistic.Name, Statistic<*>> {

        return linkedMapOf(
                Statistic.Name.DURATION to TimeStatistic(duration),
                Statistic.Name.DISTANCE to UnitStatistic(distance, distanceUnit),
                Statistic.Name.AVG_SPEED to UnitStatistic(avgSpeed, speedUnit),
                Statistic.Name.MAX_SPEED to UnitStatistic(maxSpeed, speedUnit),
                Statistic.Name.AVG_ALTITUDE to UnitStatistic(avgAltitude, AppUnit.Distance.M),
                Statistic.Name.MAX_ALTITUDE to UnitStatistic(maxAltitude, AppUnit.Distance.M),
                Statistic.Name.MIN_ALTITUDE to UnitStatistic(minAltitude, AppUnit.Distance.M),
                Statistic.Name.START_TIME to StringStatistic(startTimeStr)
        )
    }

    fun toExtendedRouteData(points: MutableList<LatLng>?  = null,
                            photos: MutableList<RoutePhoto>? = null): ExtendedRouteData {

        return ExtendedRouteData.Instance(
                name = name,
                distance = distance,
                avgSpeed = avgSpeed,
                duration = duration,
                startTime = startTime,
                startTimeStr = startTimeStr,
                maxSpeed = maxSpeed,
                avgAltitude = avgAltitude,
                maxAltitude = maxAltitude,
                minAltitude = minAltitude,
                points = points ?: emptyList(),
                photos = photos ?: emptyList()
        )
    }
}