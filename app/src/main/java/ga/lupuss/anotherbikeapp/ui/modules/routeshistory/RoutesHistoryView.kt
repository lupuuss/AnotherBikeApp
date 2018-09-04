package ga.lupuss.anotherbikeapp.ui.modules.routeshistory

import ga.lupuss.anotherbikeapp.base.BaseView
import ga.lupuss.anotherbikeapp.base.LabeledView

interface RoutesHistoryView: BaseView, LabeledView {

    var isNoDataTextVisible: Boolean
    var isRoutesHistoryVisible: Boolean
    var isRoutesHistoryProgressBarVisible: Boolean

    fun refreshRecyclerAdapter()
    fun notifyRecyclerItemChanged(position: Int)
    fun notifyRecyclerItemRemoved(position: Int, size: Int)
    fun notifyRecyclerItemInserted(position: Int, size: Int)
}