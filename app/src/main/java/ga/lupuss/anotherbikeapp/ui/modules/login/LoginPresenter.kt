package ga.lupuss.anotherbikeapp.ui.modules.login

import ga.lupuss.anotherbikeapp.Message
import ga.lupuss.anotherbikeapp.base.Presenter
import ga.lupuss.anotherbikeapp.models.base.AuthInteractor
import javax.inject.Inject

class LoginPresenter @Inject constructor(private val loginInteractor: AuthInteractor,
                                         loginView: LoginView)
    : Presenter<LoginView>(), AuthInteractor.OnLoginDoneListener {

    init {
        view = loginView
    }

    fun onClickSignIn(email: String, password: String) {

        val isEmailBlank = email.isBlank()
        val isPasswordBlank = password.isBlank()

        if (isEmailBlank || isPasswordBlank) {

            if (isEmailBlank) {
                view.emailFieldError(Message.CANNOT_BE_BLANK)
            }

            if (isPasswordBlank) {

                view.passwordFieldError(Message.CANNOT_BE_BLANK)
            }

        } else if (!view.isOnline()) {

            view.postMessage(Message.NO_INTERNET_CONNECTION)

        } else {

            view.isUiEnable = false
            view.isSignInProgressBarVisible = true
            view.isSignInButtonTextVisible = false
            loginInteractor.login(email, password, this, view)
        }
    }

    fun onClickCreateAccount() {

        view.startCreateAccountActivity()
    }

    private fun onAnyError() {
        view.isUiEnable = true
        view.isSignInProgressBarVisible = false
        view.isSignInButtonTextVisible = true
    }

    override fun onSuccess() {
        view.startMainActivity()
        view.finishActivity()
    }

    override fun onUserNotExists() {

        onAnyError()
        view.emailFieldError(Message.USER_NOT_EXISTS)
    }

    override fun onInvalidCredentialsError() {
        onAnyError()
        view.emailFieldError(Message.EMAIL_IS_INCORRECT)
        view.passwordFieldError(Message.PASSWORD_IS_INCORRECT)
    }

    override fun onUndefinedError() {
        onAnyError()
        view.postMessage(Message.SOMETHING_GOES_WRONG)
    }
}