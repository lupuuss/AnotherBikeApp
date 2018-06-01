package ga.lupuss.anotherbikeapp.ui.modules.summary

import com.google.android.gms.maps.model.LatLng
import ga.lupuss.anotherbikeapp.base.BaseView
import ga.lupuss.anotherbikeapp.models.dataclass.Statistic

interface SummaryView : BaseView {

    var isRouteEditLineVisible: Boolean
    var isStatsFragmentVisible: Boolean
    var isProgressBarVisible: Boolean

    fun showRouteLine(points: List<LatLng>)
    fun showStatistics(statistics: Map<Statistic.Name, Statistic<*>>)
    fun showRejectDialog(onYes: () -> Unit)
    fun getRouteNameFromEditText(): String
}