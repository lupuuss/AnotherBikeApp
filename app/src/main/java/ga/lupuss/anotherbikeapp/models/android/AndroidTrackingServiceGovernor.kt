package ga.lupuss.anotherbikeapp.models.android

import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import ga.lupuss.anotherbikeapp.base.BaseActivity
import ga.lupuss.anotherbikeapp.kotlin.Resettable
import ga.lupuss.anotherbikeapp.kotlin.ResettableManager
import ga.lupuss.anotherbikeapp.models.base.StringsResolver
import ga.lupuss.anotherbikeapp.models.base.TrackingServiceGovernor
import ga.lupuss.anotherbikeapp.models.base.TrackingServiceInteractor
import ga.lupuss.anotherbikeapp.models.dataclass.Statistic
import ga.lupuss.anotherbikeapp.models.trackingservice.TrackingService
import ga.lupuss.anotherbikeapp.ui.TrackingNotification
import ga.lupuss.anotherbikeapp.ui.modules.main.MainActivity
import ga.lupuss.anotherbikeapp.ui.modules.main.MainPresenter
import ga.lupuss.anotherbikeapp.ui.modules.tracking.TrackingActivity
import timber.log.Timber

class AndroidTrackingServiceGovernor(
        private val stringsResolver: StringsResolver,
        private val trackingNotification: TrackingNotification

) : TrackingServiceGovernor(), ServiceConnection, TrackingServiceInteractor.OnStatsUpdateListener {


    private val resettableManager = ResettableManager()
    private var serviceParentActivity: BaseActivity by Resettable(resettableManager)

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

        serviceBinder?.removeOnStatsUpdateListener(this)

        if (isFinishing && isServiceActive) {

            Timber.v("Finishing activity...")
            stopTracking()

        } else if (isServiceActive) {

            Timber.v("Recreating activity...")
            unbindTrackingService()

        } else {

            Timber.v("No service. Clean destroy.")
        }

        trackingNotification.clearReferences()
        resettableManager.reset()
    }

    override fun startTracking(onTrackingRequestDone: OnTrackingRequestDone?) {

        fun initTracking(){

            onServiceConnected = onTrackingRequestDone
            startTrackingService()
            bindTrackingService()
        }

        when {
            serviceBinder != null -> onTrackingRequestDone?.onTrackingRequestDone() // Tracking activity connected to existing service

            serviceParentActivity.checkLocationPermission() -> initTracking() // Starting new tracking service and tracking activity

            else -> serviceParentActivity.requestLocationPermission {
                if (it) {

                    initTracking()

                } else {

                    onTrackingRequestDone?.onTrackingRequestNoPermission()
                }
            }
        }


    }

    private fun startTrackingService() {

        Timber.v("Starting service...")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            serviceParentActivity.startForegroundService(
                    Intent(serviceParentActivity, TrackingService::class.java)
            )

        } else {

            serviceParentActivity.startService(
                    Intent(serviceParentActivity, TrackingService::class.java)
            )
        }
    }

    private fun bindTrackingService()  {

        Timber.v("Binding service...")

        serviceParentActivity.bindService(
                Intent(serviceParentActivity, TrackingService::class.java),
                this,
                Context.BIND_AUTO_CREATE
        )
    }

    private fun generateNotificationIntent(): PendingIntent {
        val intent = MainActivity.newIntent(serviceParentActivity)
        intent.putExtra(MainActivity.REQUEST_CODE_KEY, MainPresenter.Request.TRACKING_NOTIFICATION_REQUEST)
        return PendingIntent
                .getActivity(serviceParentActivity,
                        0,
                        intent,
                        PendingIntent.FLAG_UPDATE_CURRENT)
    }

    override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {

        p1 ?: throw IllegalStateException("Service should always return IBinder!")

        Timber.v("Service connected")

        serviceBinder = p1 as TrackingService.ServiceBinder

        serviceBinder!!.initNotification(
                TrackingNotification.ID,
                trackingNotification.build(
                        serviceParentActivity,
                        stringsResolver,
                        serviceBinder!!.lastStats,
                        generateNotificationIntent()
                )
        )

        serviceBinder!!.addOnStatsUpdateListener(this)

        onServiceConnected?.onTrackingRequestDone()
        isServiceActive = true
        onServiceConnected = null
    }

    override fun onStatsUpdate(stats: Map<Statistic.Name, Statistic<*>>) {

        (serviceParentActivity.getSystemService(Service.NOTIFICATION_SERVICE) as NotificationManager)
                .notify(TrackingNotification.ID, trackingNotification.build(
                        serviceParentActivity, stringsResolver, stats, generateNotificationIntent()
                ))

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

        Timber.v("Stopping service...")
        serviceParentActivity.stopService(
                Intent(serviceParentActivity, TrackingService::class.java)
        )
    }

    private fun unbindTrackingService() {

        Timber.v("Unbinding service...")
        serviceParentActivity.unbindService(this)
    }

    companion object {
        const val IS_SERVICE_ACTIVE_KEY = "mainPresenterState"
    }
}