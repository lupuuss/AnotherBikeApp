package ga.lupuss.anotherbikeapp.ui.modules.createaccount

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import ga.lupuss.anotherbikeapp.Message
import ga.lupuss.anotherbikeapp.models.base.AuthInteractor
import org.junit.Test

class CreateAccountPresenterTest {

    private val authInteractor: AuthInteractor = mock {  }
    private val createAccountView: CreateAccountView = mock { on { isOnline() }.then { true } }
    private val createAccountPresenter = CreateAccountPresenter(authInteractor, createAccountView)

    @Test
    fun onClickSignIn_shouldFinishActivity() {

        createAccountPresenter.onClickSignIn()
        verify(createAccountView, times(1)).finishActivity()
    }

    @Test
    fun onClickCreateNewAccount_shouldPostMessageBlankIfAnyFieldIsBlank() {

        createAccountPresenter.onClickCreateNewAccount("", "", "")

        verify(createAccountView, times(1)).postMessage(Message.FILL_ALL_FIELDS)
    }

    @Test
    fun onClickCreateNewAccount_shouldPostMessageNoInternetConnectionIfNoInternet() {

        val createView = mock<CreateAccountView> { on { isOnline() }.then { false } }
        val createAccountPresenter = CreateAccountPresenter(
                authInteractor,
                createView
        )

        createAccountPresenter
                .onClickCreateNewAccount("not blank", "not blank", "not blank")

        verify(createView, times(1)).postMessage(Message.NO_INTERNET_CONNECTION)
    }

    @Test
    fun onClickCreateNewAccount_shouldInitSignInAndDisableUiIfEverythingIsOk() {

        createAccountPresenter
                .onClickCreateNewAccount("correct@email.com", "moreThan6Chars","not blank")

        verify(createAccountView, times(1)).isUiEnable = false
        verify(createAccountView, times(1)).isCreateAccountProgressBarVisible = true
        verify(createAccountView, times(1)).isCreateAccountButtonTextVisible = false
        verify(authInteractor, times(1)).createAccount(
                "correct@email.com",
                "moreThan6Chars",
                "not blank",
                createAccountPresenter,
                createAccountView
        )
    }

    @Test
    fun onSuccess_shouldPostMessageAccountCreatedAndFinishActivity() {
        createAccountPresenter.onSuccess()

        verify(createAccountView, times(1)).postMessage(Message.ACCOUNT_CREATED)
        verify(createAccountView, times(1)).finishActivity()
    }

    @Test
    fun onUserExist_shouldEnableUiAndPostMessageUserExists() {

        createAccountPresenter.onUserExist()

        verify(createAccountView, times(1)).isUiEnable = true
        verify(createAccountView, times(1)).isCreateAccountProgressBarVisible = false
        verify(createAccountView, times(1)).isCreateAccountButtonTextVisible = true
        verify(createAccountView, times(1)).postMessage(Message.USER_EXISTS)
    }

    @Test
    fun onUndefinedError_shouldEnableUiAndPostSomethingGoesWrongMessage() {

        createAccountPresenter.onUndefinedError()

        verify(createAccountView, times(1)).isUiEnable = true
        verify(createAccountView, times(1)).isCreateAccountProgressBarVisible = false
        verify(createAccountView, times(1)).isCreateAccountButtonTextVisible = true
        verify(createAccountView, times(1)).postMessage(Message.SOMETHING_GOES_WRONG)
    }
}