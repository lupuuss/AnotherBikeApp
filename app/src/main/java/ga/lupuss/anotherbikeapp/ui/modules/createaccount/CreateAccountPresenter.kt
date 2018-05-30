package ga.lupuss.anotherbikeapp.ui.modules.createaccount

import ga.lupuss.anotherbikeapp.R
import ga.lupuss.anotherbikeapp.base.Presenter
import ga.lupuss.anotherbikeapp.models.AuthInteractor
import ga.lupuss.anotherbikeapp.models.firebase.FirebaseAuthInteractor
import javax.inject.Inject

class CreateAccountPresenter @Inject constructor(firebaseAuthInteractor: FirebaseAuthInteractor)
    : Presenter, AuthInteractor.OnAccountCreateDoneListener {

    private val authInteractor: AuthInteractor = firebaseAuthInteractor

    @Inject
    lateinit var createAccountView: CreateAccountView

    fun onClickSignIn() {

        createAccountView.finishActivity()
    }

    fun onClickCreateNewAccount(email: String, password: String, displayName: String) {

        if (email.isBlank() || password.isBlank() || displayName.isBlank()) {

            createAccountView.makeToast(R.string.fillAllFileds)

        } else if (!createAccountView.isOnline()) {

            createAccountView.makeToast(R.string.noInternetConnection)

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
        createAccountView.makeToast(R.string.accountCreated)
        createAccountView.finishActivity()
    }

    override fun onUndefinedError() {
        onAnyError()
        createAccountView.makeToast(R.string.somethingGoesWrong)
    }
}