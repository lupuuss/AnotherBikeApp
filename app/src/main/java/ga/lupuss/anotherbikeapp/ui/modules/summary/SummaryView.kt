package ga.lupuss.anotherbikeapp.ui.modules.summary

import com.google.android.gms.maps.model.LatLng
import ga.lupuss.anotherbikeapp.base.BaseView
import ga.lupuss.anotherbikeapp.models.dataclass.RoutePhoto
import ga.lupuss.anotherbikeapp.models.dataclass.Statistic

interface SummaryView : BaseView {

    var isRouteEditLineVisible: Boolean
    var isStatsFragmentVisible: Boolean
    var isProgressBarVisible: Boolean
    var isRejectActionVisible: Boolean
    var isSaveActionVisible: Boolean

    fun showRejectDialog(onYes: () -> Unit)
    fun showDeleteDialog(onYes: () -> Unit)
    fun showUnsavedStateDialog(onYes: () -> Unit)
    fun showRouteLine(points: List<LatLng>)
    fun showStatistics(statistics: Map<Statistic.Name, Statistic<*>>)
    fun getRouteNameFromEditText(): String
    fun setNameLabelValue(value: String)
    fun setPhotosAdaptersCallbacks(routePhotoCallback: (Int) -> RoutePhoto, routePhotosSizeCallback: () -> Int)
}