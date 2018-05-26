package ga.lupuss.anotherbikeapp.ui.modules.main

import android.content.ServiceConnection
import ga.lupuss.anotherbikeapp.base.BaseView
import ga.lupuss.anotherbikeapp.models.trackingservice.TrackingService

interface MainView : BaseView {

    fun startTrackingActivity(serviceBinder: TrackingService.ServiceBinder?)
    fun requestLocationPermission(onLocationPermissionRequestResult: (Boolean) -> Unit)
    fun startTrackingService()
    fun bindTrackingService(connection: ServiceConnection)
    fun unbindTrackingService(connection: ServiceConnection)
    fun stopTrackingService()
    fun setTrackingButtonState(trackingInProgress: Boolean)
    fun startSummaryActivity()
    fun refreshRecyclerAdapter()
    fun notifyRecyclerItemChanged(position: Int)
    fun notifyRecyclerItemRemoved(position: Int)
    fun notifyRecyclerItemInserted(position: Int)
    fun setNoDataTextVisibility(visibility: Int)
    fun setRoutesHistoryVisibility(visibility: Int)
}
