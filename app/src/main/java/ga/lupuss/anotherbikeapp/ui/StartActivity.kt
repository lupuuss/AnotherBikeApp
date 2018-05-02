package ga.lupuss.anotherbikeapp.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import ga.lupuss.anotherbikeapp.AnotherBikeApp
import ga.lupuss.anotherbikeapp.R
import ga.lupuss.anotherbikeapp.base.BaseActivity
import ga.lupuss.anotherbikeapp.ui.modules.login.LoginActivity
import ga.lupuss.anotherbikeapp.ui.modules.main.MainActivity

class StartActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        Handler().postDelayed({

            val intent: Intent =
                    if (AnotherBikeApp.get(this.application).mainComponent == null) {
                        LoginActivity.newIntent(this)
                    } else {
                        MainActivity.newIntent(this)
                    }
            finish()
            startActivity(intent)
        }, 1000)
    }
}
