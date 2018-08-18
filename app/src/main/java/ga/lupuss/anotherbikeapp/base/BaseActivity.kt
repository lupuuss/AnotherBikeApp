package ga.lupuss.anotherbikeapp.base

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.location.LocationProvider
import android.net.ConnectivityManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.PermissionChecker
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.widget.Toast
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.LatLng
import ga.lupuss.anotherbikeapp.AnotherBikeApp
import ga.lupuss.anotherbikeapp.Message
import ga.lupuss.anotherbikeapp.R
import ga.lupuss.anotherbikeapp.models.SignInVerifier
import ga.lupuss.anotherbikeapp.models.base.StringsResolver
import ga.lupuss.anotherbikeapp.ui.extensions.checkPermission
import javax.inject.Inject

abstract class BaseActivity : AppCompatActivity(), BaseView {

    private lateinit var toast: Toast

    @Inject
    lateinit var stringsResolver: StringsResolver

    private val locationPermissionRequestCode = 1
    private var onLocationPermissionRequestResult: ((Boolean) -> Unit)? = null
    protected var requiresPassedVerification = false
    protected var verificationPassed = false
    private lateinit var signInVerifier: SignInVerifier

    private enum class HomeActionMode {
        DRAWER_TOGGLE, BACK
    }

    private lateinit var mode: HomeActionMode
    private var toggle: ActionBarDrawerToggle? = null

    @SuppressLint("ShowToast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        toast = Toast.makeText(this, "empty", Toast.LENGTH_LONG)
        signInVerifier = AnotherBikeApp.get(application).signInVerifier

        if (requiresPassedVerification) {

            verificationPassed = signInVerifier.verifySignedIn(this)
        }


        if (verificationPassed || !requiresPassedVerification) {

            onCreatePostVerification(savedInstanceState)
        }
    }

    open fun onCreatePostVerification(savedInstanceState: Bundle?) {}

    override fun onResume() {
        super.onResume()

        if (verificationPassed || !requiresPassedVerification) {

            onResumePostVerification()
        }
    }

    open fun onResumePostVerification() {}

    override fun onDestroy() {
        super.onDestroy()

        if (verificationPassed || !requiresPassedVerification) {

            onDestroyPostVerification()
        }
    }

    open fun onDestroyPostVerification() {}

    fun requiresVerification() {

        requiresPassedVerification = true
    }

    fun activateToolbar(toolbar: Toolbar) {

        activateToolbar(toolbar, null)
    }

    fun activateToolbar(toolbar: Toolbar, drawerLayout: DrawerLayout?) {

        setSupportActionBar(toolbar)

        this.mode = if (drawerLayout != null) HomeActionMode.DRAWER_TOGGLE else HomeActionMode.BACK

        drawerLayout?.let {

            toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
            drawerLayout.addDrawerListener(toggle!!)
            toggle!!.syncState()
        }

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    override fun provideLocationPermission(onLocationPermissionRequestResult: (isSuccessful: Boolean) -> Unit) {

        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {

            onLocationPermissionRequestResult.invoke(true)

        } else {

            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), locationPermissionRequestCode)

            this.onLocationPermissionRequestResult = onLocationPermissionRequestResult
        }
    }

    override fun provideLocationPermission(onLocationPermissionGranted: () -> Unit, onLocationPermissionRefused: () -> Unit) {

        if (checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {

            onLocationPermissionGranted.invoke()

        } else {

            ActivityCompat.requestPermissions(this,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), locationPermissionRequestCode)

            this.onLocationPermissionRequestResult = {

                if (it) {
                    onLocationPermissionGranted.invoke()
                } else {
                    onLocationPermissionRefused.invoke()
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == locationPermissionRequestCode) {

            onLocationPermissionRequestResult
                    ?.invoke(grantResults[0] == PermissionChecker.PERMISSION_GRANTED)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        return if (mode == HomeActionMode.DRAWER_TOGGLE) {

            toggle!!.onOptionsItemSelected(item)


        } else {

            if (item.itemId == android.R.id.home) {

                onBackPressed()
            }
            super.onOptionsItemSelected(item)
        }
    }

    override fun makeToast(str: String) {
        toast.setText(str)
        toast.show()
    }

    override fun postMessage(message: Message) {

        makeToast(stringsResolver.resolve(message))
    }

    override fun finishActivity() {
        finish()
    }

    override fun isOnline(): Boolean {

        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.activeNetworkInfo
        return netInfo != null && netInfo.isConnectedOrConnecting
    }

    @SuppressLint("MissingPermission")
    override fun requestSingleLocationUpdate(onComplete: (Boolean, Location?) -> Unit) {

        LocationServices.getFusedLocationProviderClient(this).lastLocation.addOnCompleteListener {

            onComplete.invoke(it.isSuccessful, it.result)
        }
    }
}
