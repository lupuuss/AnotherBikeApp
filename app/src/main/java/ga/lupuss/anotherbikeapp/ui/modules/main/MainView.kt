package ga.lupuss.anotherbikeapp.ui.modules.main

import ga.lupuss.anotherbikeapp.base.BaseView

interface MainView : BaseView {

    fun startTrackingActivity()
    fun startSettingsActivity()
    fun startSummaryActivity()

    fun setTrackingButtonState(trackingInProgress: Boolean)

    val isDrawerLayoutOpened: Boolean
    fun setDrawerHeaderInfo(displayName: String?, email: String?)
    fun startLoginActivity()
    fun showExitWarningDialog(onYesClick: () -> Unit)
    fun hideDrawer()
    fun startAboutAppActivity()
}
