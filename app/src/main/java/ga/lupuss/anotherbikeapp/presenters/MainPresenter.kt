package ga.lupuss.anotherbikeapp.presenters

import android.Manifest
import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import ga.lupuss.anotherbikeapp.R
import ga.lupuss.anotherbikeapp.trackingservice.TrackingService
import ga.lupuss.anotherbikeapp.ui.activities.MainActivity
import ga.lupuss.anotherbikeapp.ui.activities.TrackingActivity

/**
 * Presenter associated with [MainActivity]. [MainActivity] must implement [MainPresenter.IView].
 * @param serviceActiveArg previous (before recreate) service state
 */
class MainPresenter(private val mainView: IView,
                    serviceActiveArg: Boolean) : Presenter {

    interface IView : Presenter.BaseView {
        fun onServiceStatusChanged(status: Boolean)
        fun startTrackingActivity(serviceBinder: TrackingService.ServiceBinder?)
        fun checkPermission(permission: String): Boolean
        fun requestLocationPermission(onLocationPermissionRequestResult: (Boolean) -> Unit)
        fun startTrackingService()
        fun bindTrackingService(connection: ServiceConnection)
        fun unbindTrackingService(connection: ServiceConnection)
        fun stopTrackingService()
    }

    private var isServiceActive = serviceActiveArg
        set(value) {
            mainView.onServiceStatusChanged(value)
            field = value
        }

    private var serviceBinder: TrackingService.ServiceBinder? = null

    /** Connection callback to bindService */
    private val serviceConnection = object : ServiceConnection {

        override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {

            p1 ?: throw IllegalStateException("Service should always return IBinder!")

            Log.d(MainPresenter::class.qualifiedName, "Service connected")

            serviceBinder = p1 as TrackingService.ServiceBinder

            if (!isServiceActive) {

                isServiceActive = true
                mainView.startTrackingActivity(serviceBinder)
            }
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            isServiceActive = false
            serviceBinder = null
        }
    }

    fun onClickTrackingButton() {

        when {
            serviceBinder != null -> mainView.startTrackingActivity(serviceBinder) // Tracking activity connected to existing service

            mainView.checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) -> startTracking() // Starting new tracking service and tracking activity

            else -> mainView.requestLocationPermission {
                if (it) {

                    startTracking()

                } else {

                    mainView.makeToast(R.string.message_no_permission_error)
                }
            }
        }
    }

    override fun notifyOnCreate() {

        if (isServiceActive) {

            mainView.bindTrackingService(serviceConnection)
        }

    }

    override fun notifyOnResult(requestCode: Int, resultCode: Int) {

        if (requestCode == MainActivity.Request.TRACKING_ACTIVITY_REQUEST) {

            when (resultCode) {
                TrackingActivity.Result.DONE -> {

                    Log.d(MainPresenter::class.qualifiedName, "Service done. Data may be saved")
                    stopTracking()
                }

                TrackingActivity.Result.NO_DATA_DONE -> {

                    Log.d(MainPresenter::class.qualifiedName, "Service done, but no data")
                    stopTracking()
                }

                TrackingActivity.Result.NOT_DONE -> {

                    Log.d(MainPresenter::class.qualifiedName, "Service is working...")
                }
            }
        }
    }

    private fun startTracking() {
        mainView.startTrackingService()
        mainView.bindTrackingService(serviceConnection)
    }

    private fun unbindTrackingService() {
        serviceBinder = null
        mainView.unbindTrackingService(serviceConnection)
    }

    private fun stopTracking() {
        isServiceActive = false
        serviceBinder = null
        mainView.unbindTrackingService(serviceConnection)
        mainView.stopTrackingService()
    }

    override fun notifyOnDestroy(isFinishing: Boolean) {

        if (isFinishing && isServiceActive) {

            Log.d(MainPresenter::class.qualifiedName, "Finishing activity...")
            stopTracking()

        } else if (isServiceActive) {

            Log.d(MainPresenter::class.qualifiedName, "Recreating activity...")
            unbindTrackingService()

        } else {
            Log.d(MainPresenter::class.qualifiedName, "No service. Clean destroy.")
        }
    }
}
