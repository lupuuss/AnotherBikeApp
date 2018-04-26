package ga.lupuss.anotherbikeapp.ui.modules.summary

import com.google.android.gms.maps.model.LatLng
import ga.lupuss.anotherbikeapp.base.BaseView
import ga.lupuss.anotherbikeapp.models.trackingservice.statisticsmanager.statistics.Statistic

interface SummaryView : BaseView {

    fun showRouteLine(points: List<LatLng>)
    fun showStatistics(statistics: Map<Statistic.Name, Statistic>)
    fun finishActivity()
    fun showRejectDialog(onYes: () -> Unit)
}