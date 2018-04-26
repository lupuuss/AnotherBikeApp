package ga.lupuss.anotherbikeapp.ui.modules.main

import android.Manifest
import android.content.ComponentName
import android.content.Context
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import ga.lupuss.anotherbikeapp.R
import ga.lupuss.anotherbikeapp.TEMP_ROUTE_FILE_PREFIX
import ga.lupuss.anotherbikeapp.base.Presenter
import ga.lupuss.anotherbikeapp.models.FilesManager
import ga.lupuss.anotherbikeapp.models.RoutesKeeper
import ga.lupuss.anotherbikeapp.models.trackingservice.TrackingService
import ga.lupuss.anotherbikeapp.ui.extensions.checkPermission
import ga.lupuss.anotherbikeapp.ui.modules.tracking.TrackingActivity
import timber.log.Timber

import javax.inject.Inject

/**
 * Presenter associated with [MainActivity]. [MainActivity] must implement [MainView].
 */
class MainPresenter @Inject constructor(private val context: Context,
                                        private val routesKeeper: RoutesKeeper) : Presenter {

    @Inject
    lateinit var mainView: MainView

    private var isServiceActive: Boolean = false
        set(value) {

            if (::mainView.isInitialized) mainView.setTrackingButtonState(value)
            field = value
        }

    private var serviceBinder: TrackingService.ServiceBinder? = null

    /** Connection callback to bindService */
    private val serviceConnection = object : ServiceConnection {

        override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {

            p1 ?: throw IllegalStateException("Service should always return IBinder!")

            Timber.d("Service connected")

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

            context.checkPermission(Manifest.permission.ACCESS_FINE_LOCATION) -> startTracking() // Starting new tracking service and tracking activity

            else -> mainView.requestLocationPermission {
                if (it) {

                    startTracking()

                } else {

                    mainView.makeToast(R.string.message_no_permission_error)
                }
            }
        }
    }

    override fun notifyOnCreate(savedInstanceState: Bundle?) {

        savedInstanceState?.getBoolean(IS_SERVICE_ACTIVE)?.let {
            isServiceActive = it
        }

        if (isServiceActive) {

            mainView.bindTrackingService(serviceConnection)
        }
    }

    override fun notifyOnResult(requestCode: Int, resultCode: Int) {

        if (requestCode == MainActivity.Request.TRACKING_ACTIVITY_REQUEST) {

            when (resultCode) {

                TrackingActivity.Result.DONE -> {

                    Timber.d("Service done. Data may be saved")

                    val routeData = serviceBinder?.routeData

                    stopTracking()

                    routeData?.let {

                        val path = routesKeeper.saveRouteTemporary(routeData)
                        mainView.startSummaryActivity(path.absolutePath)
                    }
                }

                TrackingActivity.Result.NO_DATA_DONE -> {

                    Timber.d("Service done, but no data")
                    stopTracking()
                }

                TrackingActivity.Result.NOT_DONE -> {

                    Timber.d("Service is working...")
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

    override fun notifyOnSavedInstanceState(outState: Bundle) {

        super.notifyOnSavedInstanceState(outState)
        outState.putBoolean(IS_SERVICE_ACTIVE, isServiceActive)
    }

    override fun notifyOnDestroy(isFinishing: Boolean) {

        if (isFinishing && isServiceActive) {

            Timber.d("Finishing activity...")
            stopTracking()

        } else if (isServiceActive) {

            Timber.d("Recreating activity...")
            unbindTrackingService()

        } else {

            Timber.d("No service. Clean destroy.")
        }
    }

    companion object {
        private const val IS_SERVICE_ACTIVE = "is service active"
    }
}
