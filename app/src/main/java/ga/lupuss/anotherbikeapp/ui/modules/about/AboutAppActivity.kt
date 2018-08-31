package ga.lupuss.anotherbikeapp.ui.modules.about

import android.content.Context
import android.content.Intent
import android.os.Bundle
import ga.lupuss.anotherbikeapp.AnotherBikeApp
import ga.lupuss.anotherbikeapp.R
import ga.lupuss.anotherbikeapp.base.ThemedActivity
import kotlinx.android.synthetic.main.activity_about_app.*
import javax.inject.Inject

class AboutAppActivity : ThemedActivity(), AboutAppView {

    @Inject
    lateinit var presenter: AboutAppPresenter

    override fun onCreate(savedInstanceState: Bundle?) {

        requiresVerification()
        super.onCreate(savedInstanceState)
    }

    override fun onCreatePostVerification(savedInstanceState: Bundle?) {

        AnotherBikeApp
                .get(application)
                .aboutAppComponent(this)
                .inject(this)

        super.onCreatePostVerification(savedInstanceState)
        setContentView(R.layout.activity_about_app)

        activateToolbar(aboutAppToolbar)

        presenter.notifyOnViewReady()
    }

    override fun onDestroyPostVerification() {

        super.onDestroyPostVerification()
        presenter.notifyOnViewReady()
    }

    companion object {

        @JvmStatic
        fun newIntent(context: Context) = Intent(context, AboutAppActivity::class.java)
    }
}
