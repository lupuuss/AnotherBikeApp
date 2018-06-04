package ga.lupuss.anotherbikeapp.ui.modules.createaccount

import ga.lupuss.anotherbikeapp.Message
import ga.lupuss.anotherbikeapp.base.Presenter
import ga.lupuss.anotherbikeapp.models.base.AuthInteractor
import ga.lupuss.anotherbikeapp.models.base.StringsResolver
import javax.inject.Inject

class CreateAccountPresenter @Inject constructor(private val stringsResolver: StringsResolver,
                                                 private val authInteractor: AuthInteractor)
    : Presenter, AuthInteractor.OnAccountCreateDoneListener {


    @Inject
    lateinit var createAccountView: CreateAccountView

    fun onClickSignIn() {

        createAccountView.finishActivity()
    }

    fun onClickCreateNewAccount(email: String, password: String, displayName: String) {

        if (email.isBlank() || password.isBlank() || displayName.isBlank()) {

            createAccountView.postMessage(Message.FILL_ALL_FIELDS)

        } else if (!createAccountView.isOnline()) {

            createAccountView.postMessage(Message.NO_INTERNET_CONNECTION)

        } else {

            createAccountView.isUiEnable = false
            createAccountView.isCreateAccountButtonTextVisible = false
            createAccountView.isCreateAccountProgressBarVisible = true
            authInteractor.createAccount(email, password, displayName, this)
        }
    }

    private fun onAnyError() {
        createAccountView.isUiEnable = true
        createAccountView.isCreateAccountButtonTextVisible = false
        createAccountView.isCreateAccountProgressBarVisible = false
    }

    override fun onSuccess() {
        createAccountView.postMessage(Message.ACCOUNT_CREATED)
        createAccountView.finishActivity()
    }

    override fun onUserExist() {
        onAnyError()
        createAccountView.postMessage(Message.USER_EXISTS)
    }

    override fun onUndefinedError() {
        onAnyError()
        createAccountView.postMessage(Message.SOMETHING_GOES_WRONG)
    }
}