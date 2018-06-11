package ga.lupuss.anotherbikeapp.models.android

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import ga.lupuss.anotherbikeapp.base.BaseActivity
import ga.lupuss.anotherbikeapp.kotlin.Resettable
import ga.lupuss.anotherbikeapp.kotlin.ResettableManager
import ga.lupuss.anotherbikeapp.models.base.TrackingServiceGovernor
import ga.lupuss.anotherbikeapp.models.base.TrackingServiceInteractor
import ga.lupuss.anotherbikeapp.models.trackingservice.TrackingService
import timber.log.Timber

class AndroidTrackingServiceGovernor : TrackingServiceGovernor(), ServiceConnection {


    private val resettebleManager = ResettableManager()
    private var serviceParentActivity: BaseActivity by Resettable(resettebleManager)

    override var serviceBinder: TrackingService.ServiceBinder? = null
        private set

    override val serviceInteractor
        get() = serviceBinder as TrackingServiceInteractor?


    private var onServiceConnected: OnTrackingRequestDone? = null

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

        resettebleManager.reset()

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

    override fun startTracking(onTrackingRequestDone: OnTrackingRequestDone?) {

        fun initTracking(){

            onServiceConnected = onTrackingRequestDone
            startTrackingService()
            bindTrackingService()
        }

        when {
            serviceBinder != null -> onTrackingRequestDone?.onTrackingInitDone() // Tracking activity connected to existing service

            serviceParentActivity.checkLocationPermission() -> initTracking() // Starting new tracking service and tracking activity

            else -> serviceParentActivity.requestLocationPermission {
                if (it) {

                    initTracking()

                } else {

                    onTrackingRequestDone?.onNoTrackingPermission()
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
                this,
                Context.BIND_AUTO_CREATE
        )
    }

    override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {

        p1 ?: throw IllegalStateException("Service should always return IBinder!")

        Timber.d("Service connected")

        serviceBinder = p1 as TrackingService.ServiceBinder

        if (!isServiceActive) {

            isServiceActive = true
            onServiceConnected?.onTrackingInitDone()
            onServiceConnected = null
        }
    }

    override fun onServiceDisconnected(p0: ComponentName?) {
        isServiceActive = false
        serviceBinder = null
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
        serviceParentActivity.unbindService(this)
    }

    companion object {
        private const val IS_SERVICE_ACTIVE_KEY = "mainPresenterState"
    }
}