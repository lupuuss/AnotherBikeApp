package ga.lupuss.anotherbikeapp.base

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Location
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.PermissionChecker
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.view.MenuItem
import android.widget.Toast
import com.google.android.gms.location.LocationServices
import ga.lupuss.anotherbikeapp.AnotherBikeApp
import ga.lupuss.anotherbikeapp.Message
import ga.lupuss.anotherbikeapp.R
import ga.lupuss.anotherbikeapp.models.SignInVerifier
import ga.lupuss.anotherbikeapp.models.base.ResourceResolver
import ga.lupuss.anotherbikeapp.ui.extensions.checkPermission
import javax.inject.Inject
import android.app.Activity
import android.provider.MediaStore
import androidx.core.content.FileProvider
import android.view.View
import android.view.inputmethod.InputMethodManager
import ga.lupuss.anotherbikeapp.ui.modules.about.AboutAppActivity
import ga.lupuss.anotherbikeapp.ui.modules.createaccount.CreateAccountActivity
import ga.lupuss.anotherbikeapp.ui.modules.forgotpassword.ForgotPasswordActivity
import ga.lupuss.anotherbikeapp.ui.modules.login.LoginActivity
import ga.lupuss.anotherbikeapp.ui.modules.main.MainActivity
import ga.lupuss.anotherbikeapp.ui.modules.settings.SettingsActivity
import ga.lupuss.anotherbikeapp.ui.modules.summary.SummaryActivity


abstract class BaseActivity : AppCompatActivity(), BaseView {

    private lateinit var toast: Toast

    @Inject
    lateinit var resourceResolver: ResourceResolver

    private val locationPermissionRequestCode = 1
    private var onLocationPermissionRequestResult: ((Boolean) -> Unit)? = null
    protected var requiresPassedVerification = false
    protected var verificationPassed = false
    private lateinit var signInVerifier: SignInVerifier
    private var photoRequest: BaseView.PhotoRequest? = null

    private enum class HomeActionMode {
        DRAWER_TOGGLE, BACK
    }

    private lateinit var mode: HomeActionMode
    private var toggle: ActionBarDrawerToggle? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            PHOTO_REQUEST_CODE -> {

                photoRequest?.onRequestDone(resultCode == Activity.RESULT_OK)
                photoRequest = null
            }
        }
    }

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

        makeToast(resourceResolver.resolve(message))
    }

    override fun finishActivity() {
        finish()
    }

    override fun isOnline(): Boolean {

        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.activeNetworkInfo
        return netInfo != null && netInfo.isConnected
    }

    @SuppressLint("MissingPermission")
    override fun requestSingleLocationUpdate(onComplete: (Boolean, Location?) -> Unit) {

        LocationServices.getFusedLocationProviderClient(this).lastLocation.addOnCompleteListener {

            onComplete.invoke(it.isSuccessful, it.result)
        }
    }

    override fun redirectToUrl(url: String) {

        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    }

    fun hideSoftKeyboard() {

        val imm = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager

        var view = currentFocus

        if (view == null) {
            view = View(this)
        }

        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    override fun requestPhoto(photoRequest: BaseView.PhotoRequest) {

        val photoIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        if (photoIntent.resolveActivity(packageManager) != null) {

            photoIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            photoIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            photoRequest.file.parentFile.mkdirs()
            photoRequest.file.createNewFile()

            val uri = FileProvider.getUriForFile(
                    this,
                    "ga.lupuss.fileprovider",
                    photoRequest.file
            )

            this.photoRequest = photoRequest

            photoIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
            startActivityForResult(photoIntent, PHOTO_REQUEST_CODE)

        } else {
            // TODO No photo messsage
        }

    }

    override fun startMainActivity() {

        AnotherBikeApp.get(this.application).initUserComponent()
        startActivity(MainActivity.newIntent(this))
    }

    override fun startCreateAccountActivity() {

        startActivity(CreateAccountActivity.newIntent(this))
    }

    override fun startForgotPasswordActivity() {

        startActivity(ForgotPasswordActivity.newIntent(this))
    }

    override fun startSettingsActivity() {
        startActivity(SettingsActivity.newIntent(this))
    }

    override fun startLoginActivity() {

        startActivity(LoginActivity.newIntent(this))
    }

    override fun startAboutAppActivity() {
        startActivity(AboutAppActivity.newIntent(this))
    }

    override fun startSummaryActivity() {

        startActivity(SummaryActivity.newIntent(this))
    }

    override fun startSummaryActivity(documentReference: String) {
        startActivity(SummaryActivity.newIntent(this, documentReference))
    }

    companion object {

        private const val PHOTO_REQUEST_CODE = 12
    }
}
