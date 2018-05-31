package ga.lupuss.anotherbikeapp.ui.modules.createaccount

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.EditText
import ga.lupuss.anotherbikeapp.AnotherBikeApp
import ga.lupuss.anotherbikeapp.R
import ga.lupuss.anotherbikeapp.base.BaseActivity
import kotlinx.android.synthetic.main.activity_create_account.*
import kotlinx.android.synthetic.main.email_edit_text.*
import kotlinx.android.synthetic.main.password_edit_text.*
import javax.inject.Inject

class CreateAccountActivity : BaseActivity(), CreateAccountView {

    @Inject
    lateinit var createAccountPresenter: CreateAccountPresenter

    override var isUiEnable: Boolean = true
        set(value) {

            createNewAccountButton?.isEnabled = value
            passwordEditText?.isEnabled = value
            emailEditText?.isEnabled = value
            displayNameEditText?.isEnabled = value
            createNewAccountButton?.isEnabled = value
            field = value
        }

    override var isCreateAccountProgressBarVisible: Boolean = false
        set(value) {

            createAccountProgressBar?.let {
                it.visibility = if (value) View.VISIBLE else View.GONE
            }

            field = value
        }

    override var isCreateAccountButtonTextVisible: Boolean = true
        set(value) {
            val color = signInButton.currentTextColor
            val trans = if (value) 255 else 0

            createNewAccountButton.setTextColor(
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
        // Dagger MUST be first
        DaggerCreateAccountComponent
                .builder()
                .createAccountModule(CreateAccountModule(this))
                .anotherBikeAppComponent(
                        (this.application as AnotherBikeApp).anotherBikeAppComponent
                )
                .build()
                .inject(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)
        savedInstanceState?.let {
            isUiEnable = it[IS_UI_ENABLE_KEY] as Boolean
            isCreateAccountButtonTextVisible = it[IS_CREATE_ACCOUNT_BUTTON_TEXT_VISIBLE] as Boolean
            isCreateAccountProgressBarVisible = it[IS_CREATE_ACCOUNT_PROGRESSBAR_VISIBLE] as Boolean
        }
    }

    fun onClickCreateNewAccount(@Suppress("UNUSED_PARAMETER") view: View) {

        createAccountPresenter.onClickCreateNewAccount(
                findViewById<EditText>(R.id.emailEditInclude).text.toString(),
                findViewById<EditText>(R.id.passwordEditInclude).text.toString(),
                displayNameEditText.text.toString()
        )
    }

    fun onClickSignIn(@Suppress("UNUSED_PARAMETER") view: View) {

        createAccountPresenter.onClickSignIn()
    }

    companion object {

        const val IS_UI_ENABLE_KEY = "isUiEnable"
        const val IS_CREATE_ACCOUNT_PROGRESSBAR_VISIBLE = "isSignInProgressBarVisible"
        const val IS_CREATE_ACCOUNT_BUTTON_TEXT_VISIBLE = "isSignInButtonTextVisible"

        @JvmStatic
        fun newIntent(context: Context) =
                Intent(context, CreateAccountActivity::class.java)
    }
}
