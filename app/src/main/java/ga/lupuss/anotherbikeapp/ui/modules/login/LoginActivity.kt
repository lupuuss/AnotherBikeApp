package ga.lupuss.anotherbikeapp.ui.modules.login

import android.content.Context
import android.content.Intent
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        DaggerLoginComponent
                .builder()
                .loginModule(LoginModule(this))
                .coreComponent(getAnotherBikeApp().coreComponent)
                .build()
                .inject(this)
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

        @JvmStatic
        fun newIntent(context: Context) =
                Intent(context, LoginActivity::class.java)
    }
}
