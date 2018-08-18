package ga.lupuss.anotherbikeapp.ui.modules.settings

import android.content.Context
import android.content.Intent
import android.os.Bundle
import ga.lupuss.anotherbikeapp.AnotherBikeApp
import ga.lupuss.anotherbikeapp.R
import ga.lupuss.anotherbikeapp.base.ThemedBaseActivity
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : ThemedBaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {

        requiresVerification()
        super.onCreate(savedInstanceState)
    }

    override fun onCreatePostVerification(savedInstanceState: Bundle?) {

        // Dagger MUST be first
        // super method requires it

        DaggerSettingsComponent
                .builder()
                .userComponent((this.application as AnotherBikeApp).userComponent!!)
                .build()
                .inject(this)

        super.onCreatePostVerification(savedInstanceState)

        setContentView(R.layout.activity_settings)
        activateToolbar(toolbarSettings)
    }

    companion object {

        @JvmStatic
        fun newIntent(context: Context) = Intent(context, SettingsActivity::class.java)
    }
}
