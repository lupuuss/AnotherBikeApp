package ga.lupuss.anotherbikeapp.ui.modules.main

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorInflater
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v4.content.PermissionChecker
import android.util.Log
import android.view.View
import android.widget.Toast

import ga.lupuss.anotherbikeapp.R
import ga.lupuss.anotherbikeapp.trackingservice.TrackingService
import ga.lupuss.anotherbikeapp.ui.modules.tracking.TrackingActivity

import kotlinx.android.synthetic.main.activity_main.trackingButton
import javax.inject.Inject

/**
 * Main user's interface.
 */
class MainActivity : AppCompatActivity(), MainPresenter.IView {

    @Inject
    lateinit var mainPresenter: MainPresenter
    private lateinit var toast: Toast

    private val locationPermissionRequestCode = 1
    private var onLocationPermissionRequestResult: ((Boolean) -> Unit)? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == Request.TRACKING_ACTIVITY_REQUEST) {

            mainPresenter.notifyOnResult(requestCode, resultCode)
        }

    }

    @SuppressLint("ShowToast")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        toast = Toast.makeText(this, "", Toast.LENGTH_LONG)

        mainPresenter.notifyOnCreate(savedInstanceState)

    }

    override fun onSaveInstanceState(outState: Bundle?) {

        super.onSaveInstanceState(outState)
        mainPresenter.notifyOnSavedInstanceState(outState!!)
    }

    override fun onDestroy() {
        super.onDestroy()
        mainPresenter.notifyOnDestroy(isFinishing)
        Log.d(MainActivity::class.qualifiedName, "MainActivity destroyed!")
    }

    // onClicks

    fun onClickTrackingButton(view: View) {

        val animator = AnimatorInflater.loadAnimator(this, R.animator.tracking_button)
        animator.setTarget(view)
        animator.addListener(object : Animator.AnimatorListener {

            override fun onAnimationRepeat(p0: Animator?) = Unit

            override fun onAnimationCancel(p0: Animator?) = Unit

            override fun onAnimationStart(p0: Animator?) = Unit

            override fun onAnimationEnd(p0: Animator?) {

                mainPresenter.onClickTrackingButton()
            }

        })

        animator.start()
    }

    // MainPresenter.IView Impl

    override fun setTrackingButtonState(trackingInProgress: Boolean) {

        trackingButton.setText(
                if (trackingInProgress) R.string.continue_tracking else R.string.start_tracking
        )
    }

    override fun makeToast(stringId: Int) {
        toast.setText(stringId)
        toast.show()
    }

    override fun makeToast(str: String) {
        toast.setText(str)
        toast.show()
    }

    override fun startTrackingActivity(serviceBinder: TrackingService.ServiceBinder?) {

        startActivityForResult(
                TrackingActivity.newIntent(this@MainActivity, serviceBinder!!),
                Request.TRACKING_ACTIVITY_REQUEST
        )
    }

    override fun checkPermission(permission: String): Boolean {

        val permissionStatus = ContextCompat
                .checkSelfPermission(this, permission)

        return permissionStatus == PackageManager.PERMISSION_GRANTED
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

    override fun startTrackingService() {

        // after bind onServiceStart is called by serviceConnection callback

        Log.d(MainActivity::class.qualifiedName, "Starting service...")
        startService(Intent(this, TrackingService::class.java))
    }

    override fun bindTrackingService(connection: ServiceConnection) {

        Log.d(MainActivity::class.qualifiedName, "Binding service...")
        bindService(
                Intent(this, TrackingService::class.java),
                connection,
                Context.BIND_AUTO_CREATE
        )
    }

    override fun stopTrackingService() {

        Log.d(MainActivity::class.qualifiedName, "Stopping service...")
        stopService(Intent(this, TrackingService::class.java))
    }

    override fun unbindTrackingService(connection: ServiceConnection) {

        Log.d(MainActivity::class.qualifiedName, "Unbinding service...")
        unbindService(connection)
    }

    class Request {
        companion object {
            const val TRACKING_ACTIVITY_REQUEST = 0
        }
    }
}
