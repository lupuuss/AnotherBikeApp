package ga.lupuss.anotherbikeapp.ui.modules.login

import android.content.Context
import ga.lupuss.anotherbikeapp.R
import ga.lupuss.anotherbikeapp.base.Presenter
import ga.lupuss.anotherbikeapp.models.AuthInteractor
import ga.lupuss.anotherbikeapp.models.firebase.FirebaseAuthInteractor
import javax.inject.Inject

class LoginPresenter @Inject constructor() : Presenter, AuthInteractor.OnLoginDoneListener {

    @Inject
    lateinit var loginView: LoginView

    @Inject
    lateinit var context: Context

    @Inject
    lateinit var loginInteractor: FirebaseAuthInteractor

    fun onClickSignIn(email: String, password: String) {

        if (email.isBlank() || password.isBlank()) {

            loginView.makeToast(R.string.passwordOrEmailBlank)

        } else if (!loginView.isOnline()) {

            loginView.makeToast(R.string.noInternetConnection)

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

    override fun onUndefinedError() {
        onAnyError()
        loginView.makeToast(R.string.somethingGoesWrong)
    }

    override fun onIncorrectCredentialsError() {
        onAnyError()
        loginView.makeToast(R.string.wrongCredentials)
    }
}