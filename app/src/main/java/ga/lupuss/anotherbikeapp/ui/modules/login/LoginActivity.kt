package ga.lupuss.anotherbikeapp.ui.modules.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import ga.lupuss.anotherbikeapp.AnotherBikeApp
import ga.lupuss.anotherbikeapp.R
import ga.lupuss.anotherbikeapp.base.BaseActivity
import ga.lupuss.anotherbikeapp.ui.modules.main.MainActivity
import javax.inject.Inject

class LoginActivity : BaseActivity(), LoginView {

    @Inject
    lateinit var loginPresenter: LoginPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        DaggerLoginComponent
                .builder()
                .loginModule(LoginModule(this))
                .build()
                .inject(this)
    }

    @Suppress("UNUSED_PARAMETER")
    fun onClickUseWithoutAccount(view: View) {

        loginPresenter.onClickUseWithoutAccount()
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
