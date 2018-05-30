package ga.lupuss.anotherbikeapp.ui.modules.main

import android.content.ServiceConnection
import ga.lupuss.anotherbikeapp.base.BaseView
import ga.lupuss.anotherbikeapp.models.trackingservice.TrackingService

interface MainView : BaseView {

    fun startTrackingActivity(serviceBinder: TrackingService.ServiceBinder?)
    fun startSummaryActivity()
    fun startTrackingService()
    fun bindTrackingService(connection: ServiceConnection)
    fun unbindTrackingService(connection: ServiceConnection)
    fun stopTrackingService()

    fun requestLocationPermission(onLocationPermissionRequestResult: (Boolean) -> Unit)
    fun setTrackingButtonState(trackingInProgress: Boolean)

    fun refreshRecyclerAdapter()
    fun notifyRecyclerItemChanged(position: Int)
    fun notifyRecyclerItemRemoved(position: Int)
    fun notifyRecyclerItemInserted(position: Int)

    fun setNoDataTextVisibility(visibility: Int)
    fun setRoutesHistoryVisibility(visibility: Int)
    fun setProgressBarVisibility(visibility: Int)
    fun setDrawerHeaderInfos(displayName: String?, email: String?)
    fun startLoginActivity()
}
