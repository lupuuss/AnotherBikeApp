package ga.lupuss.anotherbikeapp.ui.modules.login

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.EditText
import ga.lupuss.anotherbikeapp.AnotherBikeApp
import ga.lupuss.anotherbikeapp.R
import ga.lupuss.anotherbikeapp.base.BaseActivity
import ga.lupuss.anotherbikeapp.ui.extensions.isGone
import ga.lupuss.anotherbikeapp.ui.modules.createaccount.CreateAccountActivity
import ga.lupuss.anotherbikeapp.ui.modules.main.MainActivity
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.email_edit_text.*
import kotlinx.android.synthetic.main.password_edit_text.*
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
                it.isGone = !value
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

    fun onClickSignIn(view: View) {

        loginPresenter.onClickSignIn(
                findViewById<EditText>(R.id.emailEditInclude).text.toString(),
                findViewById<EditText>(R.id.passwordEditInclude).text.toString()
        )
    }

    fun onClickCreateAccount(view: View) {
        loginPresenter.onClickCreateAccount()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // Dagger MUST be first
        DaggerLoginComponent
                .builder()
                .loginModule(LoginModule(this))
                .anotherBikeAppComponent(getAnotherBikeApp().anotherBikeAppComponent)
                .build()
                .inject(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

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

    override fun startMainActivity() {

        startActivity(MainActivity.newIntent(this))
    }

    override fun startCreateAccountActivity() {

        startActivity(CreateAccountActivity.newIntent(this))
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
