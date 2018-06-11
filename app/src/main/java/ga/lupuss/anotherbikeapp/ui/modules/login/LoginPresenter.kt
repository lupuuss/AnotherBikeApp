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

        if (email.isBlank() || password.isBlank()) {

            view.postMessage(Message.EMAIL_OR_PASSWORD_BLANK)

        } else if (!view.isOnline()) {

            view.postMessage(Message.NO_INTERNET_CONNECTION)

        } else {

            view.isUiEnable = false
            view.isSignInProgressBarVisible = true
            view.isSignInButtonTextVisible = false
            loginInteractor.login(email, password, this)
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

    }

    override fun onIncorrectCredentialsError() {
        onAnyError()
        view.postMessage(Message.INCORRECT_CREDENTIALS)
    }

    override fun onUndefinedError() {
        onAnyError()
        view.postMessage(Message.SOMETHING_GOES_WRONG)
    }
}