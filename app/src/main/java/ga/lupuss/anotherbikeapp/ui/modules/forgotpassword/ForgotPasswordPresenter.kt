package ga.lupuss.anotherbikeapp.ui.modules.forgotpassword

import ga.lupuss.anotherbikeapp.Message
import ga.lupuss.anotherbikeapp.base.Presenter
import ga.lupuss.anotherbikeapp.models.base.AuthInteractor
import javax.inject.Inject

class ForgotPasswordPresenter @Inject constructor(
        forgotPasswordView: ForgotPasswordView,
        private val authInteractor: AuthInteractor
) : Presenter<ForgotPasswordView>(), AuthInteractor.OnPasswordResetDoneListener {

    init {
        view = forgotPasswordView
    }

    fun onClickReset(email: String) {

        if (email.isBlank()) {

            view.emailFieldError(Message.CANNOT_BE_BLANK)

        } else if (!view.isOnline()) {

            view.postMessage(Message.NO_INTERNET_CONNECTION)

        } else {

            authInteractor.resetPassword(email, this, view)
        }
    }

    override fun onSuccess() {
        view.postMessage(Message.PASSWORD_RESET_SUCCESS)
        view.finishActivity()
    }

    override fun onUserNotExists() {

        view.emailFieldError(Message.USER_NOT_EXISTS)
    }

    override fun onEmailBadlyFormatted() {

        view.emailFieldError(Message.INCORRECT_EMAIL_FORMAT)
    }

    override fun onUndefinedError() {

        view.postMessage(Message.SOMETHING_GOES_WRONG)
    }
}