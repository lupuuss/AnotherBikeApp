package ga.lupuss.anotherbikeapp.base

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.widget.Toast
import android.net.ConnectivityManager
import android.support.v4.app.ActivityCompat
import android.support.v4.content.PermissionChecker
import ga.lupuss.anotherbikeapp.AppTheme
import ga.lupuss.anotherbikeapp.Message
import ga.lupuss.anotherbikeapp.R
import ga.lupuss.anotherbikeapp.models.interfaces.PreferencesInteractor
import ga.lupuss.anotherbikeapp.models.interfaces.StringsResolver
import ga.lupuss.anotherbikeapp.ui.extensions.checkPermission
import ga.lupuss.anotherbikeapp.ui.modules.main.MainActivity
import javax.inject.Inject


abstract class BaseActivity : AppCompatActivity(), BaseView, PreferencesInteractor.OnThemeChangedListener {

    private lateinit var toast: Toast

    @Inject
    lateinit var stringsResolver: StringsResolver

    @Inject
    lateinit var preferencesInteractor: PreferencesInteractor

    private val locationPermissionRequestCode = 1
    private var onLocationPermissionRequestResult: ((Boolean) -> Unit)? = null

    @SuppressLint("ShowToast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toast = Toast.makeText(this, "empty", Toast.LENGTH_LONG)
        setTheme(preferencesInteractor.appTheme)
        preferencesInteractor.addOnThemeChangedListener(this, this)
    }

    fun activateToolbar(toolbar: Toolbar) {
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    override fun requestLocationPermission(onLocationPermissionRequestResult: (Boolean) -> Unit) {

        ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), locationPermissionRequestCode)

        this.onLocationPermissionRequestResult = onLocationPermissionRequestResult
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == locationPermissionRequestCode) {

            onLocationPermissionRequestResult
                    ?.invoke(grantResults[0] == PermissionChecker.PERMISSION_GRANTED)
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        preferencesInteractor.removeOnThemeChangedListener(this)
    }

    private fun setTheme(theme: AppTheme) {

        setTheme(when (theme) {
            AppTheme.GREY -> R.style.GreyTheme
            AppTheme.ORANGE -> R.style.OrangeTheme
        })
    }

    private fun applyTheme(theme: AppTheme) {
        setTheme(theme)
        recreate()
    }

    override fun onThemeChanged(theme: AppTheme) {

        applyTheme(theme)
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

        makeToast(stringsResolver.resolve(message))
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