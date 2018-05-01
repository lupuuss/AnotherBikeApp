package ga.lupuss.anotherbikeapp.models.pojo

import com.google.android.gms.maps.model.LatLng
import ga.lupuss.anotherbikeapp.models.trackingservice.statisticsmanager.statistics.Statistic
import ga.lupuss.anotherbikeapp.models.trackingservice.statisticsmanager.statistics.StringStatistic
import ga.lupuss.anotherbikeapp.models.trackingservice.statisticsmanager.statistics.TimeStatistic
import ga.lupuss.anotherbikeapp.models.trackingservice.statisticsmanager.statistics.UnitStatistic

data class SerializableRouteData(
        var name: String?,
        val savedRoute: MutableList<LatLng>,
        var distance: Double,
        var avgSpeed: Double,
        var maxSpeed: Double,
        var duration: Long,
        var startTimeStr: String,
        var startTime: Long
) {
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
