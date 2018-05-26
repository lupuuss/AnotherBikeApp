package ga.lupuss.anotherbikeapp.models.pojo

import ga.lupuss.anotherbikeapp.models.trackingservice.statisticsmanager.statistics.Statistic
import ga.lupuss.anotherbikeapp.models.trackingservice.statisticsmanager.statistics.StringStatistic
import ga.lupuss.anotherbikeapp.models.trackingservice.statisticsmanager.statistics.TimeStatistic
import ga.lupuss.anotherbikeapp.models.trackingservice.statisticsmanager.statistics.UnitStatistic

open class RouteData(
        name: String?,
        distance: Double,
        avgSpeed: Double,
        duration: Long,
        startTime: Long,
        var startTimeStr: String,
        var maxSpeed: Double
) : ShortRouteData(name, distance, avgSpeed, duration, startTime) {
    fun getStatisticsMap(speedUnit: Statistic.Unit, distanceUnit: Statistic.Unit)
            : Map<Statistic.Name, Statistic> {

        return linkedMapOf(
                Statistic.Name.DISTANCE to UnitStatistic(distance, distanceUnit),
                Statistic.Name.AVG_SPEED to UnitStatistic(avgSpeed, speedUnit),
                Statistic.Name.MAX_SPEED to UnitStatistic(maxSpeed, speedUnit),
                Statistic.Name.DURATION to TimeStatistic(duration),
                Statistic.Name.START_TIME to StringStatistic(startTimeStr)
        )
    }
}