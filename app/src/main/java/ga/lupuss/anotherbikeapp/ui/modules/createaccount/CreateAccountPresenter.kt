package ga.lupuss.anotherbikeapp.ui.modules.createaccount

import ga.lupuss.anotherbikeapp.Message
import ga.lupuss.anotherbikeapp.base.Presenter
import ga.lupuss.anotherbikeapp.models.base.AuthInteractor
import javax.inject.Inject

class CreateAccountPresenter @Inject constructor(private val authInteractor: AuthInteractor,
                                                 createAccountView: CreateAccountView)

    : Presenter<CreateAccountView>(), AuthInteractor.OnAccountCreationDoneListener {


    init {
        view = createAccountView
    }

    fun onClickSignIn() {

        view.finishActivity()
    }

    fun onClickCreateNewAccount(email: String, password: String, displayName: String) {

        val isEmailBlank = email.isBlank()
        val isPasswordBlank = password.isBlank()
        val isDisplayNameBlank = displayName.isBlank()

        if (isEmailBlank || isPasswordBlank || isDisplayNameBlank) {

            if (isEmailBlank) {
                view.emailFieldError(Message.CANNOT_BE_BLANK)
            }

            if (isPasswordBlank) {
                view.passwordFieldError(Message.CANNOT_BE_BLANK)
            }


            if (isDisplayNameBlank) {
                view.displayNameFieldError(Message.CANNOT_BE_BLANK)
            }

        } else if (!view.isOnline()) {

            view.postMessage(Message.NO_INTERNET_CONNECTION)

        } else {

            view.isUiEnable = false
            view.isCreateAccountButtonTextVisible = false
            view.isCreateAccountProgressBarVisible = true
            authInteractor.createAccount(email, password, displayName, this, view)
        }
    }

    private fun onAnyError() {
        view.isUiEnable = true
        view.isCreateAccountButtonTextVisible = true
        view.isCreateAccountProgressBarVisible = false
    }

    override fun onSuccess() {
        view.postMessage(Message.ACCOUNT_CREATED)
        view.finishActivity()
    }

    override fun onUserExist() {
        onAnyError()
        view.emailFieldError(Message.USER_EXISTS)
    }

    override fun onUndefinedError() {
        onAnyError()
        view.postMessage(Message.SOMETHING_GOES_WRONG)
    }

    override fun onInvalidCredentialsError() {
        onAnyError()
        view.emailFieldError(Message.INCORRECT_EMAIL_FORMAT)
    }

    override fun onTooWeakPassword() {
        onAnyError()
        view.passwordFieldError(Message.PASSWORD_IS_TOO_WEAK)
    }
}