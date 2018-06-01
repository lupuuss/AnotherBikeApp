package ga.lupuss.anotherbikeapp.ui.modules.main

import ga.lupuss.anotherbikeapp.base.BaseView

interface MainView : BaseView {

    fun startTrackingActivity()
    fun startSummaryActivity()

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
    fun showExitWarningDialog(onYesClick: () -> Unit)
    fun startSettingsActivity()
}
