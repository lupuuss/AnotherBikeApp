package ga.lupuss.anotherbikeapp.ui.modules.login

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import ga.lupuss.anotherbikeapp.AnotherBikeApp
import ga.lupuss.anotherbikeapp.R
import ga.lupuss.anotherbikeapp.base.BaseActivity
import ga.lupuss.anotherbikeapp.ui.modules.main.MainActivity
import kotlinx.android.synthetic.main.activity_login.*
import javax.inject.Inject

@Suppress("UNUSED_PARAMETER")
class LoginActivity : BaseActivity(), LoginView {

    @Inject
    lateinit var loginPresenter: LoginPresenter

    override var isUiEnable: Boolean = true
        set(value) {

            signInButton?.isEnabled = value
            passwordEditText?.isEnabled = value
            emailEditText?.isEnabled = value
            createNewAccountButton?.isEnabled = value
            field = value
        }

    override var isSignInProgressBarVisible: Boolean = false
        set(value) {

            signInProgressBar?.let {
                it.visibility = if (value) View.VISIBLE else View.GONE
            }

            field = value
        }

    override var isSignInButtonTextVisible: Boolean = true
        set(value) {
            val color = signInButton.currentTextColor
            val trans = if (value) 255 else 0

            signInButton.setTextColor(
                    Color.argb(
                            trans,
                            Color.red(color),
                            Color.green(color),
                            Color.blue(color)
                    )
            )
            field = value
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        DaggerLoginComponent
                .builder()
                .loginModule(LoginModule(this))
                .anotherBikeAppComponent(getAnotherBikeApp().anotherBikeAppComponent)
                .build()
                .inject(this)

        savedInstanceState?.let {
            isUiEnable = it[IS_UI_ENABLE_KEY] as Boolean
            isSignInButtonTextVisible = it[IS_SIGN_IN_BUTTON_TEXT_VISIBLE] as Boolean
            isSignInProgressBarVisible = it[IS_SIGN_IN_PROGRESSBAR_VISIBLE] as Boolean
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)

        outState?.let {
            it.putBoolean(IS_UI_ENABLE_KEY, isUiEnable)
            it.putBoolean(IS_SIGN_IN_PROGRESSBAR_VISIBLE, isSignInProgressBarVisible)
            it.putBoolean(IS_SIGN_IN_BUTTON_TEXT_VISIBLE, isSignInButtonTextVisible)
        }
    }

    fun onClickSignIn(view: View) {

        loginPresenter.onClickSignIn(emailEditText.text.toString(),
                passwordEditText.text.toString())
    }

    override fun startMainActivity() {

        startActivity(MainActivity.newIntent(this))
    }

    override fun getAnotherBikeApp(): AnotherBikeApp = AnotherBikeApp.get(this.application)

    companion object {

        const val IS_UI_ENABLE_KEY = "isUiEnable"
        const val IS_SIGN_IN_PROGRESSBAR_VISIBLE = "isSignInProgressBarVisible"
        const val IS_SIGN_IN_BUTTON_TEXT_VISIBLE = "isSignInButtonTextVisible"

        @JvmStatic
        fun newIntent(context: Context) =
                Intent(context, LoginActivity::class.java)
    }
}
