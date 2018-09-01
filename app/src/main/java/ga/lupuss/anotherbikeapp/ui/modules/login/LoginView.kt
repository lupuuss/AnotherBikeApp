package ga.lupuss.anotherbikeapp.ui.modules.login

import ga.lupuss.anotherbikeapp.AnotherBikeApp
import ga.lupuss.anotherbikeapp.Message
import ga.lupuss.anotherbikeapp.base.BaseView

interface LoginView : BaseView {
    fun getAnotherBikeApp(): AnotherBikeApp
    fun startMainActivity()
    fun startCreateAccountActivity()
    fun emailFieldError(message: Message)
    fun passwordFieldError(message: Message)
    fun startForgotPasswordActivity()

    var isUiEnable: Boolean
    var isSignInProgressBarVisible: Boolean
    var isSignInButtonTextVisible: Boolean
}