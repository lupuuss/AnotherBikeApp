package ga.lupuss.anotherbikeapp.ui.modules.tracking

import com.google.android.gms.maps.model.LatLng
import ga.lupuss.anotherbikeapp.base.BaseView
import ga.lupuss.anotherbikeapp.models.dataclass.Statistic

interface TrackingView : BaseView {

    fun updateStats(statistics: Map<Statistic.Name, Statistic<*>>)
    fun updateTrackLine(points: List<LatLng>)
    fun prepareMapToTrack(points: List<LatLng>)

    var isInfoWaitForLocationVisible: Boolean
    var isMapButtonInLockState: Boolean

    fun isTrackLineReady(): Boolean
    fun moveMapCamera(latLng: LatLng)
    fun showFinishTrackingDialog(onYesAction: () -> Unit)
    fun finishActivityWithResult(result: Int)
    fun notifyNewPhoto(position: Int, size: Int)
    fun notifyPhotoDeleted(position: Int, size: Int)
}