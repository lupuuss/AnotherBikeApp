package ga.lupuss.anotherbikeapp.ui.modules.forgotpassword

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import ga.lupuss.anotherbikeapp.AnotherBikeApp
import ga.lupuss.anotherbikeapp.Message
import ga.lupuss.anotherbikeapp.R
import ga.lupuss.anotherbikeapp.base.BaseActivity
import kotlinx.android.synthetic.main.activity_forgot_password.*
import javax.inject.Inject

class ForgotPasswordActivity : BaseActivity(), ForgotPasswordView {

    @Inject
    lateinit var presenter: ForgotPasswordPresenter

    override fun onCreate(savedInstanceState: Bundle?) {

        AnotherBikeApp.get(application)
                .forgotPasswordComponent(this)
                .inject(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)
    }

    @Suppress("UNUSED_PARAMETER")
    fun onClickReset(view: View) {

        presenter.onClickReset((emailEditInclude as EditText).text.toString())
    }

    override fun emailFieldError(message: Message) {
        (emailEditInclude as EditText).error = resourceResolver.resolve(message)
    }

    override fun onDestroy() {

        super.onDestroy()
        presenter.notifyOnDestroy(isFinishing)
    }

    companion object {

        @JvmStatic
        fun newIntent(context: Context): Intent =
                Intent(context, ForgotPasswordActivity::class.java)
    }
}
