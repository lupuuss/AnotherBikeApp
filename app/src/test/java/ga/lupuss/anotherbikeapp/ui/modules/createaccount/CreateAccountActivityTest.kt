package ga.lupuss.anotherbikeapp.ui.modules.createaccount

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
import junit.framework.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(application = TestAnotherBikeApp::class)
class CreateAccountActivityTest {

    private val activity = Robolectric.setupActivity(CreateAccountActivity::class.java)
    private val mockedPresenter = mock<CreateAccountPresenter> { }

    private fun createBundle(isUiEnable: Boolean,
                             isCreateAccountProgressBarVisible: Boolean,
                             isCreateAccountButtonVisible: Boolean): Bundle {
        val bundle = Bundle()

        bundle.putBoolean(CreateAccountActivity.IS_UI_ENABLE_KEY, isUiEnable)
        bundle.putBoolean(CreateAccountActivity.IS_CREATE_ACCOUNT_PROGRESSBAR_VISIBLE,
                isCreateAccountProgressBarVisible)
        bundle.putBoolean(CreateAccountActivity.IS_CREATE_ACCOUNT_BUTTON_TEXT_VISIBLE,
                isCreateAccountButtonVisible)

        return bundle
    }

    @Test
    fun onCreate_shouldRestoreInstanceState() {

        var activityController = Robolectric.buildActivity(CreateAccountActivity::class.java)
        var activity = activityController.create(
                createBundle(false, true, false)
        ).get()

        Assert.assertEquals(false, activity.isUiEnable)
        Assert.assertEquals(true, activity.isCreateAccountProgressBarVisible)
        Assert.assertEquals(false, activity.isCreateAccountButtonTextVisible)

        activityController = Robolectric.buildActivity(CreateAccountActivity::class.java)
        activity = activityController.create(
                createBundle(true, true, true)
        ).get()

        Assert.assertEquals(true, activity.isUiEnable)
        Assert.assertEquals(true, activity.isCreateAccountProgressBarVisible)
        Assert.assertEquals(true, activity.isCreateAccountButtonTextVisible)

        activityController = Robolectric.buildActivity(CreateAccountActivity::class.java)
        activity = activityController.create(
                createBundle(false, false, false)
        ).get()

        Assert.assertEquals(false, activity.isUiEnable)
        Assert.assertEquals(false, activity.isCreateAccountProgressBarVisible)
        Assert.assertEquals(false, activity.isCreateAccountButtonTextVisible)
    }

    @Test
    fun onDestroy_shouldNotifyPresenter() {
        val activityController = Robolectric.buildActivity(CreateAccountActivity::class.java)
        val activity = activityController
                .create()
                .start()
                .resume()
                .get()

        activity.createAccountPresenter = mockedPresenter
        activityController.pause().stop().destroy()

        verify(mockedPresenter, times(1)).notifyOnDestroy(any())
    }

    @Test
    fun onSaveInstanceState_shouldSaveInstanceState() {

        var activityController = Robolectric.buildActivity(CreateAccountActivity::class.java)
        var activity = activityController
                .create()
                .start()
                .resume()
                .get()
        activity.isUiEnable = true
        activity.isCreateAccountButtonTextVisible = true
        activity.isCreateAccountProgressBarVisible = true

        var bundle = Bundle()

        activityController.saveInstanceState(bundle)

        Assert.assertEquals(true, bundle.getBoolean(CreateAccountActivity.IS_UI_ENABLE_KEY))
        Assert.assertEquals(true, bundle.getBoolean(CreateAccountActivity.IS_CREATE_ACCOUNT_BUTTON_TEXT_VISIBLE))
        Assert.assertEquals(true, bundle.getBoolean(CreateAccountActivity.IS_CREATE_ACCOUNT_PROGRESSBAR_VISIBLE))

        activityController = Robolectric.buildActivity(CreateAccountActivity::class.java)
        activity = activityController
                .create()
                .start()
                .resume()
                .get()
        activity.isUiEnable = false
        activity.isCreateAccountButtonTextVisible = false
        activity.isCreateAccountProgressBarVisible = true

        bundle = Bundle()

        activityController.saveInstanceState(bundle)

        Assert.assertEquals(false, bundle.getBoolean(CreateAccountActivity.IS_UI_ENABLE_KEY))
        Assert.assertEquals(false, bundle.getBoolean(CreateAccountActivity.IS_CREATE_ACCOUNT_BUTTON_TEXT_VISIBLE))
        Assert.assertEquals(true, bundle.getBoolean(CreateAccountActivity.IS_CREATE_ACCOUNT_PROGRESSBAR_VISIBLE))
    }

    @Test
    fun onClickCreateNewAccount_shouldNotifyPresenter() {

        activity.createAccountPresenter = mockedPresenter
        activity.findViewById<EditText>(R.id.emailEditInclude).setText("Email")
        activity.findViewById<EditText>(R.id.passwordEditInclude).setText("Password")
        activity.findViewById<EditText>(R.id.displayNameEditText).setText("DisplayName")
        activity.onClickCreateNewAccount(mock{})

        verify(mockedPresenter, times(1))
                .onClickCreateNewAccount("Email", "Password", "DisplayName")
    }

    @Test
    fun onClickSignIn_shouldNotifyPresenter() {

        activity.createAccountPresenter = mockedPresenter
        activity.onClickSignIn(mock{})

        verify(mockedPresenter, times(1)).onClickSignIn()
    }

    @Test
    fun isUiEnable_whenTrue_shouldEnableUi() {

        activity.isUiEnable = false
        activity.isUiEnable = true

        Assert.assertEquals(true, activity.findViewById<View>(R.id.createNewAccountButton).isEnabled)
        Assert.assertEquals(true, activity.findViewById<View>(R.id.passwordEditInclude).isEnabled)
        Assert.assertEquals(true, activity.findViewById<View>(R.id.emailEditInclude).isEnabled)
        Assert.assertEquals(true, activity.findViewById<View>(R.id.displayNameEditText).isEnabled)
    }

    @Test
    fun isUiEnable_whenFalse_shouldEnableUi() {

        activity.isUiEnable = true
        activity.isUiEnable = false

        Assert.assertEquals(false, activity.findViewById<View>(R.id.createNewAccountButton).isEnabled)
        Assert.assertEquals(false, activity.findViewById<View>(R.id.passwordEditInclude).isEnabled)
        Assert.assertEquals(false, activity.findViewById<View>(R.id.emailEditInclude).isEnabled)
        Assert.assertEquals(false, activity.findViewById<View>(R.id.displayNameEditText).isEnabled)
    }

    @Test
    fun isCreateAccountProgressBarVisible_whenTrue_shouldShowProgressBar() {
        activity.isCreateAccountProgressBarVisible = false
        activity.isCreateAccountProgressBarVisible = true

        Assert.assertEquals(View.VISIBLE, activity.findViewById<View>(R.id.createAccountProgressBar).visibility)
    }

    @Test
    fun isCreateAccountProgressBarVisible_whenFalse_shouldHideProgressBar() {
        activity.isCreateAccountProgressBarVisible = true
        activity.isCreateAccountProgressBarVisible = false

        Assert.assertEquals(View.GONE, activity.findViewById<View>(R.id.createAccountProgressBar).visibility)
    }

    @Test
    fun isCreateAccountButtonTextVisible_whenTrue_shouldSetTextOpaque() {

        activity.isCreateAccountButtonTextVisible = false
        activity.isCreateAccountButtonTextVisible = true

        Assert.assertEquals(255,
                Color.alpha(activity.findViewById<TextView>(R.id.createNewAccountButton).currentTextColor))
    }

    @Test
    fun isCreateAccountButtonTextVisible_whenFalse_shouldSetTextTransparent() {

        activity.isCreateAccountButtonTextVisible = true
        activity.isCreateAccountButtonTextVisible = false

        Assert.assertEquals(0,
                Color.alpha(activity.findViewById<TextView>(R.id.createNewAccountButton).currentTextColor))
    }
}