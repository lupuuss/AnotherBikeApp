package ga.lupuss.anotherbikeapp.ui.modules.main

import ga.lupuss.anotherbikeapp.base.BaseView

interface MainView : BaseView {

    var isNoDataTextVisible: Boolean
    var isRoutesHistoryVisible: Boolean
    var isProgressBarVisible: Boolean

    fun startTrackingActivity()
    fun startSettingsActivity()
    fun startSummaryActivity()
    fun startSummaryActivity(docRef: String)

    fun setTrackingButtonState(trackingInProgress: Boolean)

    fun refreshRecyclerAdapter()
    fun notifyRecyclerItemChanged(position: Int)
    fun notifyRecyclerItemRemoved(position: Int, size: Int)
    fun notifyRecyclerItemInserted(position: Int, size: Int)

    val isDrawerLayoutOpened: Boolean
    fun setDrawerHeaderInfo(displayName: String?, email: String?)
    fun startLoginActivity()
    fun showExitWarningDialog(onYesClick: () -> Unit)
    fun hideDrawer()
}
