package ga.lupuss.anotherbikeapp.ui.modules.login

import ga.lupuss.anotherbikeapp.Message
import ga.lupuss.anotherbikeapp.base.Presenter
import ga.lupuss.anotherbikeapp.models.interfaces.AuthInteractor
import ga.lupuss.anotherbikeapp.models.firebase.FirebaseAuthInteractor
import javax.inject.Inject

class LoginPresenter @Inject constructor(private val loginInteractor: AuthInteractor) : Presenter, AuthInteractor.OnLoginDoneListener {
    @Inject
    lateinit var loginView: LoginView

    fun onClickSignIn(email: String, password: String) {

        if (email.isBlank() || password.isBlank()) {

            loginView.postMessage(Message.EMAIL_OR_PASSWORD_BLANK)

        } else if (!loginView.isOnline()) {

            loginView.postMessage(Message.NO_INTERNET_CONNECTION)

        } else {

            loginView.isUiEnable = false
            loginView.isSignInProgressBarVisible = true
            loginView.isSignInButtonTextVisible = false
            loginInteractor.login(email, password, this)
        }
    }

    fun onClickCreateAccount() {

        loginView.startCreateAccountActivity()
    }

    private fun onAnyError() {
        loginView.isUiEnable = true
        loginView.isSignInProgressBarVisible = false
        loginView.isSignInButtonTextVisible = true
    }

    override fun onSuccess() {
        loginView.startMainActivity()
        loginView.finishActivity()
    }

    override fun onUserNotExists() {

    }


    override fun onIncorrectCredentialsError() {
        onAnyError()
        loginView.postMessage(Message.INCORRECT_CREDENTIALS)
    }

    override fun onUndefinedError() {
        onAnyError()
        loginView.postMessage(Message.SOMETHING_GOES_WRONG)
    }
}