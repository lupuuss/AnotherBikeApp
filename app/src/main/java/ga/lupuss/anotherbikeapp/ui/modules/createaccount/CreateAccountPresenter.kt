package ga.lupuss.anotherbikeapp.ui.modules.createaccount

import ga.lupuss.anotherbikeapp.Message
import ga.lupuss.anotherbikeapp.base.Presenter
import ga.lupuss.anotherbikeapp.models.base.AuthInteractor
import javax.inject.Inject

class CreateAccountPresenter @Inject constructor(private val authInteractor: AuthInteractor,
                                                 createAccountView: CreateAccountView)

    : Presenter<CreateAccountView>(), AuthInteractor.OnAccountCreateDoneListener {


    init {
        view = createAccountView
    }

    fun onClickSignIn() {

        view.finishActivity()
    }

    fun onClickCreateNewAccount(email: String, password: String, displayName: String) {

        if (email.isBlank() || password.isBlank() || displayName.isBlank()) {

            view.postMessage(Message.FILL_ALL_FIELDS)

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
        view.isCreateAccountButtonTextVisible = false
        view.isCreateAccountProgressBarVisible = false
    }

    override fun onSuccess() {
        view.postMessage(Message.ACCOUNT_CREATED)
        view.finishActivity()
    }

    override fun onUserExist() {
        onAnyError()
        view.postMessage(Message.USER_EXISTS)
    }

    override fun onUndefinedError() {
        onAnyError()
        view.postMessage(Message.SOMETHING_GOES_WRONG)
    }

    override fun onInvalidCredentialsError() {
        onAnyError()
        view.postMessage(Message.INVALID_CREDENTIALS_CREATING)
    }

    override fun onTooWeakPassword() {
        onAnyError()
        view.postMessage(Message.PASSWORD_IS_TOO_WEAK)
    }
}