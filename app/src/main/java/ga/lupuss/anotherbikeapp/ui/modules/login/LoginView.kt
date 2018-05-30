package ga.lupuss.anotherbikeapp.ui.modules.login

import ga.lupuss.anotherbikeapp.AnotherBikeApp
import ga.lupuss.anotherbikeapp.base.BaseView

interface LoginView : BaseView {
    fun getAnotherBikeApp(): AnotherBikeApp
    fun startMainActivity()
    fun startCreateAccountActivity()
    var isUiEnable: Boolean
    var isSignInProgressBarVisible: Boolean
    var isSignInButtonTextVisible: Boolean
}