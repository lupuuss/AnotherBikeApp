package ga.lupuss.anotherbikeapp.ui.modules.login

import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import ga.lupuss.anotherbikeapp.Message
import ga.lupuss.anotherbikeapp.models.base.AuthInteractor
import org.junit.Test


class LoginPresenterTest {

    private val loginView: LoginView = mock { on { isOnline() }.then { true } }
    private val loginInteractor: AuthInteractor = mock { }
    private val loginPresenter = LoginPresenter(loginInteractor, loginView)


    @Test
    fun onClickSignIn_shouldPostMessageBlankIfAnyFieldIsBlank() {

        loginPresenter.onClickSignIn("", "notblank")

        verify(loginView, times(1)).postMessage(Message.EMAIL_OR_PASSWORD_BLANK)
    }

    @Test
    fun onClickSignIn_shouldPostMessageNoInternetConnectionIfNoInternet() {

        val loginView = mock<LoginView> { on { isOnline() }.then { false } }
        val loginPresenter = LoginPresenter(
                loginInteractor,
                loginView
        )

        loginPresenter.onClickSignIn("not blank", "not blank")

        verify(loginView, times(1)).postMessage(Message.NO_INTERNET_CONNECTION)
    }

    @Test
    fun onClickSignIn_shouldInitSignInIfEverythingIsOk() {

        loginPresenter.onClickSignIn("not blank", "not blank")

        verify(loginView, times(1)).isUiEnable = false
        verify(loginView, times(1)).isSignInProgressBarVisible = true
        verify(loginView, times(1)).isSignInButtonTextVisible = false
        verify(loginInteractor, times(1))
                .login("not blank", "not blank", loginPresenter)
    }

    @Test
    fun onClickCreateAccountShouldStartCreateAccountActivity() {
        loginPresenter.onClickCreateAccount()

        verify(loginView, times(1)).startCreateAccountActivity()
    }

    @Test
    fun onSuccess_shouldStartMainActivity() {
        loginPresenter.onSuccess()

        verify(loginView, times(1)).startMainActivity()
        verify(loginView, times(1)).finishActivity()
    }

    @Test
    fun onUserNotExists_should() {
    }

    @Test
    fun onIncorrectCredentialsError_shouldEnableUiAndPostIncorrectCredentialsMessage() {
        loginPresenter.onIncorrectCredentialsError()

        verify(loginView, times(1)).isUiEnable = true
        verify(loginView, times(1)).isSignInProgressBarVisible = false
        verify(loginView, times(1)).isSignInButtonTextVisible = true
        verify(loginView, times(1)).postMessage(Message.INCORRECT_CREDENTIALS)
    }

    @Test
    fun onUndefinedError_shouldEnableUiAndPostSomethingGoesWrongMessage() {

        loginPresenter.onUndefinedError()

        verify(loginView, times(1)).isUiEnable = true
        verify(loginView, times(1)).isSignInProgressBarVisible = false
        verify(loginView, times(1)).isSignInButtonTextVisible = true
        verify(loginView, times(1)).postMessage(Message.SOMETHING_GOES_WRONG)
    }
}