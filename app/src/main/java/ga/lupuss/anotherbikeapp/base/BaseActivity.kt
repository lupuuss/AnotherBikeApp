package ga.lupuss.anotherbikeapp.base

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.widget.Toast
import android.net.ConnectivityManager
import ga.lupuss.anotherbikeapp.AppTheme
import ga.lupuss.anotherbikeapp.Message
import ga.lupuss.anotherbikeapp.R
import ga.lupuss.anotherbikeapp.models.interfaces.PreferencesInteractor
import ga.lupuss.anotherbikeapp.models.interfaces.StringsResolver
import ga.lupuss.anotherbikeapp.ui.extensions.checkPermission
import javax.inject.Inject


abstract class BaseActivity : AppCompatActivity(), BaseView, PreferencesInteractor.OnThemeChangedListener {

    private lateinit var toast: Toast

    @Inject
    lateinit var messageResolver: StringsResolver

    @Inject
    lateinit var preferencesInteractor: PreferencesInteractor

    @SuppressLint("ShowToast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toast = Toast.makeText(this, "empty", Toast.LENGTH_LONG)
        setTheme(preferencesInteractor.appTheme)
        preferencesInteractor.addOnThemeChangedListener(this, this)
    }

    fun setTheme(theme: AppTheme) {

        setTheme(when (theme) {
            AppTheme.GREY -> R.style.GreyTheme
            AppTheme.ORANGE -> R.style.OrangeTheme
        })
    }

    fun applyTheme(theme: AppTheme) {
        setTheme(theme)
        recreate()
    }

    override fun onThemeChanged(theme: AppTheme) {

        applyTheme(theme)
    }

    fun activateToolbar(toolbar: Toolbar) {
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if (item.itemId == android.R.id.home) {

            onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun makeToast(str: String) {
        toast.setText(str)
        toast.show()
    }

    override fun postMessage(message: Message) {

        makeToast(messageResolver.resolve(message))
    }

    override fun checkLocationPermission(): Boolean = this.checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)

    override fun finishActivity() {
        finish()
    }

    override fun isOnline(): Boolean {

        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.activeNetworkInfo
        return netInfo != null && netInfo.isConnectedOrConnecting
    }
}