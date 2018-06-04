package ga.lupuss.anotherbikeapp.models.android

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import ga.lupuss.anotherbikeapp.base.BaseActivity
import ga.lupuss.anotherbikeapp.models.base.TrackingServiceGovernor
import ga.lupuss.anotherbikeapp.models.base.TrackingServiceInteractor
import ga.lupuss.anotherbikeapp.models.trackingservice.TrackingService
import timber.log.Timber

class AndroidTrackingServiceGovernor : TrackingServiceGovernor() {


    private lateinit var serviceParentActivity: BaseActivity

    override var serviceBinder: TrackingService.ServiceBinder? = null
        private set

    override val serviceInteractor
        get() = serviceBinder as TrackingServiceInteractor?

    /** Connection callback to bindService */
    private val serviceConnection = object : ServiceConnection {

        override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {

            p1 ?: throw IllegalStateException("Service should always return IBinder!")

            Timber.d("Service connected")

            serviceBinder = p1 as TrackingService.ServiceBinder

            if (!isServiceActive) {

                isServiceActive = true
                onServiceConnected?.invoke()
                onServiceConnected = null
            }
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            isServiceActive = false
            serviceBinder = null
        }
    }

    private var onServiceConnected: (() -> Unit)? = null

    fun init(parentActivity: BaseActivity, savedInstanceState: Bundle?) {

        serviceParentActivity = parentActivity

        isServiceActive = savedInstanceState?.getBoolean(IS_SERVICE_ACTIVE_KEY) ?: false

        if (this.isServiceActive) {

            bindTrackingService()
        }
    }

    fun saveInstanceState(outState: Bundle?) {

        outState?.putBoolean(IS_SERVICE_ACTIVE_KEY, isServiceActive)
    }

    override fun destroy(isFinishing: Boolean) {

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

    override fun startTracking(onSuccess: (() -> Unit)?, onNoPermission: (() -> Unit)?) {

        fun initTracking(){

            onServiceConnected = onSuccess
            startTrackingService()
            bindTrackingService()
        }

        when {
            serviceBinder != null -> onSuccess?.invoke() // Tracking activity connected to existing service

            serviceParentActivity.checkLocationPermission() -> initTracking() // Starting new tracking service and tracking activity

            else -> serviceParentActivity.requestLocationPermission {
                if (it) {

                    initTracking()

                } else {

                    onNoPermission?.invoke()
                }
            }
        }


    }

    private fun startTrackingService() {

        // after bind onServiceStart is called by serviceConnection callback

        Timber.d("Starting service...")
        serviceParentActivity.startService(
                Intent(serviceParentActivity, TrackingService::class.java)
        )
    }

    private fun bindTrackingService()  {

        Timber.d("Binding service...")

        serviceParentActivity.bindService(
                Intent(serviceParentActivity, TrackingService::class.java),
                serviceConnection,
                Context.BIND_AUTO_CREATE
        )
    }

    override fun stopTracking() {

        isServiceActive = false
        serviceBinder = null
        unbindTrackingService()
        stopTrackingService()
    }

    private fun stopTrackingService() {

        Timber.d("Stopping service...")
        serviceParentActivity.stopService(
                Intent(serviceParentActivity, TrackingService::class.java)
        )
    }

    private fun unbindTrackingService() {

        Timber.d("Unbinding service...")
        serviceParentActivity.unbindService(serviceConnection)
    }

    companion object {
        private const val IS_SERVICE_ACTIVE_KEY = "mainPresenterState"
    }
}