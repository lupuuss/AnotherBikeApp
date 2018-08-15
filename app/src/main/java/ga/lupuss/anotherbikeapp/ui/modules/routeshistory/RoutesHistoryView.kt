package ga.lupuss.anotherbikeapp.ui.modules.routeshistory

import ga.lupuss.anotherbikeapp.base.BaseView

interface RoutesHistoryView: BaseView {

    var isNoDataTextVisible: Boolean
    var isRoutesHistoryVisible: Boolean
    var isProgressBarVisible: Boolean

    fun refreshRecyclerAdapter()
    fun notifyRecyclerItemChanged(position: Int)
    fun notifyRecyclerItemRemoved(position: Int, size: Int)
    fun notifyRecyclerItemInserted(position: Int, size: Int)
    fun startSummaryActivity(docRef: String)
}