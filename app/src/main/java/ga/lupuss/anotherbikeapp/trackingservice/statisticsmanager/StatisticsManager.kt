package ga.lupuss.anotherbikeapp.trackingservice.statisticsmanager

import android.location.Location
import com.google.android.gms.maps.model.LatLng
import ga.lupuss.anotherbikeapp.trackingservice.statisticsmanager.statistics.Statistic

interface StatisticsManager {

    val savedRoute: List<LatLng>
    val lastStats: Map<Statistic.Name, Statistic>?
    val timer: Timer

    var lastLocation: Location?
    var minSpeedToCount: Double
    var speedUnit: Statistic.Unit
    var distanceUnit: Statistic.Unit
    var onNewStats: ((stats: Map<Statistic.Name, Statistic>) -> Unit)?

    fun pushNewLocation(location: Location)
    fun notifyLostLocation()
    fun notifyLocationOk()
}