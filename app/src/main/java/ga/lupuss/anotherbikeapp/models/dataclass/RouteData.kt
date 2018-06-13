package ga.lupuss.anotherbikeapp.models.dataclass

import com.google.android.gms.maps.model.LatLng

open class RouteData(
        name: String?,
        distance: Double,
        avgSpeed: Double,
        duration: Long,
        startTime: Long,
        val startTimeStr: String,
        val maxSpeed: Double,
        val avgAltitude: Double,
        val maxAltitude: Double,
        val minAltitude: Double
) : ShortRouteData(name, distance, avgSpeed, duration, startTime) {
    fun getStatisticsMap(speedUnit: Statistic.Unit, distanceUnit: Statistic.Unit)
            : Map<Statistic.Name, Statistic<*>> {

        return linkedMapOf(
                Statistic.Name.DISTANCE to UnitStatistic(distance, distanceUnit),
                Statistic.Name.DURATION to TimeStatistic(duration),
                Statistic.Name.AVG_SPEED to UnitStatistic(avgSpeed, speedUnit),
                Statistic.Name.MAX_SPEED to UnitStatistic(maxSpeed, speedUnit),
                Statistic.Name.AVG_ALTITUDE to UnitStatistic(avgAltitude, Statistic.Unit.M),
                Statistic.Name.MAX_ALTITUDE to UnitStatistic(maxAltitude, Statistic.Unit.M),
                Statistic.Name.MIN_ALTITUDE to UnitStatistic(minAltitude, Statistic.Unit.M),
                Statistic.Name.START_TIME to StringStatistic(startTimeStr)
        )
    }

    fun toExtendedRouteData(points: MutableList<LatLng>?): ExtendedRouteData {

        return ExtendedRouteData(
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
                points = points ?: emptyList<LatLng>().toMutableList()
        )
    }
}