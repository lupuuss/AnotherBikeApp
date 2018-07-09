package ga.lupuss.anotherbikeapp.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.support.v7.app.AppCompatActivity
import ga.lupuss.anotherbikeapp.AnotherBikeApp
import ga.lupuss.anotherbikeapp.R
import ga.lupuss.anotherbikeapp.ui.modules.login.LoginActivity
import ga.lupuss.anotherbikeapp.ui.modules.main.MainActivity

class StartActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        AnotherBikeApp.get(this.application)
                .anotherBikeAppComponent.providesAuthInteractor().isUserLogged()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        Handler().postDelayed({

            val intent: Intent = if (AnotherBikeApp.get(this.application)
                            .anotherBikeAppComponent.providesAuthInteractor().isUserLogged()) {
                MainActivity.newIntent(this)

            } else {
                LoginActivity.newIntent(this)
            }
            finish()
            startActivity(intent)

        }, 1000)
    }
}
