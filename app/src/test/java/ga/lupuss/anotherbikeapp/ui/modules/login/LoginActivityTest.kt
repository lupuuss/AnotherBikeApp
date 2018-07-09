package ga.lupuss.anotherbikeapp.ui.modules.login

import android.content.ComponentName
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import ga.lupuss.anotherbikeapp.R
import ga.lupuss.anotherbikeapp.TestAnotherBikeApp
import ga.lupuss.anotherbikeapp.ui.modules.createaccount.CreateAccountActivity
import ga.lupuss.anotherbikeapp.ui.modules.main.MainActivity
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(application = TestAnotherBikeApp::class)
class LoginActivityTest {


    private val activity = Robolectric.setupActivity(LoginActivity::class.java)
    private val mockedPresenter = mock<LoginPresenter> { }

    private fun createBundle(isUiEnable: Boolean,
                             isSignInProgressBarVisible: Boolean,
                             isSignInButtonTextVisible: Boolean): Bundle {
        val bundle = Bundle()

        bundle.putBoolean(LoginActivity.IS_UI_ENABLE_KEY, isUiEnable)
        bundle.putBoolean(LoginActivity.IS_SIGN_IN_PROGRESSBAR_VISIBLE,
                isSignInProgressBarVisible)
        bundle.putBoolean(LoginActivity.IS_SIGN_IN_BUTTON_TEXT_VISIBLE,
                isSignInButtonTextVisible)

        return bundle
    }

    @Test
    fun onCreate_shouldRestoreInstanceState() {

        var activityController = Robolectric.buildActivity(LoginActivity::class.java)
        var activity = activityController.create(
                createBundle(false, true, false)
        ).get()

        assertEquals(false, activity.isUiEnable)
        assertEquals(true, activity.isSignInProgressBarVisible)
        assertEquals(false, activity.isSignInButtonTextVisible)

        activityController = Robolectric.buildActivity(LoginActivity::class.java)
        activity = activityController.create(
                createBundle(true, true, true)
        ).get()

        assertEquals(true, activity.isUiEnable)
        assertEquals(true, activity.isSignInProgressBarVisible)
        assertEquals(true, activity.isSignInButtonTextVisible)

        activityController = Robolectric.buildActivity(LoginActivity::class.java)
        activity = activityController.create(
                createBundle(false, false, false)
        ).get()

        assertEquals(false, activity.isUiEnable)
        assertEquals(false, activity.isSignInProgressBarVisible)
        assertEquals(false, activity.isSignInButtonTextVisible)
    }

    @Test
    fun onDestroy_shouldNotifyPresenter() {
        val activityController = Robolectric.buildActivity(LoginActivity::class.java)
        val activity = activityController
                .create()
                .start()
                .resume()
                .get()

        activity.loginPresenter = mockedPresenter
        activityController.pause().stop().destroy()

        verify(mockedPresenter, times(1)).notifyOnDestroy(any())
    }

    @Test
    fun onSaveInstanceState_shouldSaveInstanceState() {

        var activityController = Robolectric.buildActivity(LoginActivity::class.java)
        var activity = activityController
                .create()
                .start()
                .resume()
                .get()
        activity.isUiEnable = true
        activity.isSignInButtonTextVisible = true
        activity.isSignInProgressBarVisible = true

        var bundle = Bundle()

        activityController.saveInstanceState(bundle)

        assertEquals(true, bundle.getBoolean(LoginActivity.IS_UI_ENABLE_KEY))
        assertEquals(true, bundle.getBoolean(LoginActivity.IS_SIGN_IN_BUTTON_TEXT_VISIBLE))
        assertEquals(true, bundle.getBoolean(LoginActivity.IS_SIGN_IN_PROGRESSBAR_VISIBLE))

        activityController = Robolectric.buildActivity(LoginActivity::class.java)
        activity = activityController
                .create()
                .start()
                .resume()
                .get()
        activity.isUiEnable = false
        activity.isSignInButtonTextVisible = false
        activity.isSignInProgressBarVisible = true

        bundle = Bundle()

        activityController.saveInstanceState(bundle)

        assertEquals(false, bundle.getBoolean(LoginActivity.IS_UI_ENABLE_KEY))
        assertEquals(false, bundle.getBoolean(LoginActivity.IS_SIGN_IN_BUTTON_TEXT_VISIBLE))
        assertEquals(true, bundle.getBoolean(LoginActivity.IS_SIGN_IN_PROGRESSBAR_VISIBLE))
    }

    @Test
    fun onClickSignIn_shouldNotifyPresenter() {

        activity.loginPresenter = mockedPresenter
        activity.findViewById<EditText>(R.id.emailEditInclude).setText("Email")
        activity.findViewById<EditText>(R.id.passwordEditInclude).setText("Password")
        activity.onClickSignIn(mock{})

        verify(mockedPresenter, times(1))
                .onClickSignIn("Email", "Password")
    }

    @Test
    fun onClickCreateAccount_shouldNotifyPresenter() {

        activity.loginPresenter = mockedPresenter
        activity.onClickCreateAccount(mock{})

        verify(mockedPresenter, times(1)).onClickCreateAccount()
    }

    @Test
    fun isUiEnable_whenTrue_shouldEnableUi() {

        activity.isUiEnable = false
        activity.isUiEnable = true

        assertEquals(true, activity.findViewById<View>(R.id.createNewAccountButton).isEnabled)
        assertEquals(true, activity.findViewById<View>(R.id.passwordEditInclude).isEnabled)
        assertEquals(true, activity.findViewById<View>(R.id.emailEditInclude).isEnabled)
    }

    @Test
    fun isUiEnable_whenFalse_shouldEnableUi() {

        activity.isUiEnable = true
        activity.isUiEnable = false

        assertEquals(false, activity.findViewById<View>(R.id.createNewAccountButton).isEnabled)
        assertEquals(false, activity.findViewById<View>(R.id.passwordEditInclude).isEnabled)
        assertEquals(false, activity.findViewById<View>(R.id.emailEditInclude).isEnabled)
    }

    @Test
    fun isSignInProgressBarVisible_whenTrue_shouldShowProgressBar() {
        activity.isSignInProgressBarVisible = false
        activity.isSignInProgressBarVisible = true

        assertEquals(View.VISIBLE, activity.findViewById<View>(R.id.signInProgressBar).visibility)
    }

    @Test
    fun isSingInProgressBarVisible_whenFalse_shouldHideProgressBar() {
        activity.isSignInProgressBarVisible = true
        activity.isSignInProgressBarVisible = false

        assertEquals(View.GONE, activity.findViewById<View>(R.id.signInProgressBar).visibility)
    }

    @Test
    fun isSignInButtonTextVisible_whenTrue_shouldSetTextOpaque() {

        activity.isSignInButtonTextVisible = false
        activity.isSignInButtonTextVisible = true

        assertEquals(255,
                Color.alpha(activity.findViewById<TextView>(R.id.signInButton).currentTextColor))
    }

    @Test
    fun isSignInButtonTextVisible_whenFalse_shouldSetTextTransparent() {

        activity.isSignInButtonTextVisible = true
        activity.isSignInButtonTextVisible = false

        assertEquals(0,
                Color.alpha(activity.findViewById<TextView>(R.id.signInButton).currentTextColor))
    }

    @Test
    fun startMainActivity_shouldStartMainActivity() {

        activity.startMainActivity()
        val shadow = Shadows.shadowOf(activity)

        assertEquals(ComponentName(activity, MainActivity::class.java), shadow.peekNextStartedActivity().component)
    }

    @Test
    fun startCreateAccountActivity_shouldStartCreateAccountActivity() {

        activity.startCreateAccountActivity()
        val shadow = Shadows.shadowOf(activity)

        assertEquals(ComponentName(activity, CreateAccountActivity::class.java), shadow.peekNextStartedActivity().component)
    }
}