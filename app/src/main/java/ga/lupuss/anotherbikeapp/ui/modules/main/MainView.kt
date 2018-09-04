package ga.lupuss.anotherbikeapp.ui.modules.main

import ga.lupuss.anotherbikeapp.base.BaseView

interface MainView : BaseView {

    fun startTrackingActivity()
    fun setTrackingButtonState(trackingInProgress: Boolean)

    val isDrawerLayoutOpened: Boolean
    fun setDrawerHeaderInfo(displayName: String?, email: String?)
    fun showExitWarningDialog(onYesClick: () -> Unit)
    fun hideDrawer()
}
