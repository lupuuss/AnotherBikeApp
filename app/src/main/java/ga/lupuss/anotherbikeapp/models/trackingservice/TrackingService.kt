package ga.lupuss.anotherbikeapp.models.trackingservice

import android.Manifest
import android.app.Service
import android.content.Intent
import android.location.Location
import android.os.*
import com.google.android.gms.location.*
import ga.lupuss.anotherbikeapp.AnotherBikeApp
import ga.lupuss.anotherbikeapp.models.base.TrackingServiceInteractor
import ga.lupuss.anotherbikeapp.models.trackingservice.statisticsmanager.StatisticsManager
import ga.lupuss.anotherbikeapp.models.dataclass.Statistic
import ga.lupuss.anotherbikeapp.models.base.PreferencesInteractor
import ga.lupuss.anotherbikeapp.ui.extensions.checkPermission
import timber.log.Timber
import javax.inject.Inject


/** Receives location from GPS and shares saved route and statistics.
 *  To control service use [TrackingServiceInteractor].
 *  To receive location changes implement [TrackingServiceInteractor.LocationDataReceiver].
 *  To receive statistics updates implement [TrackingServiceInteractor.OnStatsUpdateListener]
 */
class TrackingService : Service(), PreferencesInteractor.OnUnitChangedListener {

    @Inject
    lateinit var statsManager: StatisticsManager

    @Inject
    lateinit var locationClient: FusedLocationProviderClient

    @Inject
    lateinit var preferencesInteractor: PreferencesInteractor

    val savedRoute
        get() = statsManager.savedRoute

    val lastStats: Map<Statistic.Name, Statistic<*>>?
        get() = statsManager.lastStats

    val routeData
        get() = statsManager.routeData

    var lastLocationAvailability = false

    private val locationDataReceivers = mutableListOf<TrackingServiceInteractor.LocationDataReceiver>()
    private val onStatsUpdateListeners = mutableListOf<TrackingServiceInteractor.OnStatsUpdateListener>()

    private val thread = HandlerThread("Service HandleThread")
    private lateinit var backgroundThread: Handler
    private lateinit var uiThread: Handler

    private var locationRequested = false
    private val locationCallback: LocationCallback = object : LocationCallback() {

        override fun onLocationResult(locRes: LocationResult?) {

            super.onLocationResult(locRes)

            backgroundThread.post {

                locRes ?: return@post

                val location: Location? = locRes.lastLocation

                location ?: return@post

                statsManager.pushNewLocation(location)

                uiThread.post {

                    locationDataReceivers.forEach { it.onNewLocation(savedRoute) }
                }

                Timber.v("NEW POINT = [${location.latitude}, ${location.longitude}]")
            }
        }

        override fun onLocationAvailability(locA: LocationAvailability?) {

            super.onLocationAvailability(locA)

            val isLocationOk = locA?.isLocationAvailable ?: true

            lastLocationAvailability = isLocationOk

            backgroundThread.post {

                if (!isLocationOk && savedRoute.isNotEmpty()) {

                    statsManager.notifyLostLocation()

                } else if (isLocationOk) {

                    statsManager.notifyLocationOk()
                }
            }

            Timber.d("Location availability: %s", isLocationOk)
            locationDataReceivers.forEach { it.onLocationAvailability(isLocationOk) }
        }

    }

    inner class ServiceBinder : Binder(), TrackingServiceInteractor {

        override val lastLocationAvailability get() = this@TrackingService.lastLocationAvailability

        override val savedRoute get() = this@TrackingService.savedRoute

        override val lastStats get() = this@TrackingService.lastStats

        override val routeData get() = this@TrackingService.routeData

        override fun isServiceInProgress(): Boolean = this@TrackingService.isInProgress()

        override fun connectServiceDataReceiver(locationDataReceiver: TrackingServiceInteractor.LocationDataReceiver) =
                this@TrackingService.connectLocationDataReceiver(locationDataReceiver)

        override fun disconnectServiceDataReceiver(locationDataReceiver: TrackingServiceInteractor.LocationDataReceiver) =
                this@TrackingService.disconnectLocationDataReceiver(locationDataReceiver)

        override fun addOnStatsUpdateListener(onStatsUpdateListener: TrackingServiceInteractor.OnStatsUpdateListener) =
                this@TrackingService.addOnStatsUpdateListener(onStatsUpdateListener)

        override fun removeOnStatsUpdateListener(onStatsUpdateListener: TrackingServiceInteractor.OnStatsUpdateListener) =
                this@TrackingService.removeOnStatsUpdateListener(onStatsUpdateListener)
    }

    override fun onUnitChanged(speedUnit: Statistic.Unit, distanceUnit: Statistic.Unit) {

        statsManager.speedUnit = speedUnit
        statsManager.distanceUnit = distanceUnit
        statsManager.refresh()
    }

    override fun onCreate() {
        super.onCreate()

        DaggerTrackingServiceComponent.builder()
                .anotherBikeAppComponent(AnotherBikeApp.get(this.application).anotherBikeAppComponent)
                .build()
                .inject(this)

        uiThread = Handler(mainLooper)
        thread.start()
        backgroundThread = Handler(thread.looper)

        preferencesInteractor.addOnUnitChangedListener(this, this)

        statsManager.onNewStats = { stats ->

            uiThread.post {

                onStatsUpdateListeners.forEach { it.onStatsUpdate(stats) }
            }
        }

        Timber.d("Service started")
    }

    override fun onBind(p0: Intent?): IBinder? {

        Timber.d("Bound %s", p0)

        if (!locationRequested) {

            requestLocation()
        }
        return ServiceBinder()
    }

    override fun onUnbind(intent: Intent?): Boolean {

        Timber.d("Unbound > %s", intent)
        return super.onUnbind(intent)
    }

    private fun requestLocation() {

        if (this.checkPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {

            locationClient.requestLocationUpdates(
                    LocationRequest.create().apply {
                        interval = 1000
                        fastestInterval = 1000
                        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                    },
                    locationCallback,
                    null
            )

            Timber.d("Location request send")
        }

        locationRequested = true
    }

    override fun onDestroy() {
        super.onDestroy()

        locationClient.removeLocationUpdates(locationCallback)
        statsManager.timer.stop()
        thread.quit()
        Timber.d("Service destroyed.")
    }

    fun connectLocationDataReceiver(locationDataReceiver: TrackingServiceInteractor.LocationDataReceiver) {

        locationDataReceivers.add(locationDataReceiver)
    }

    fun disconnectLocationDataReceiver(locationDataReceiver: TrackingServiceInteractor.LocationDataReceiver) {

        locationDataReceivers.remove(locationDataReceiver)
    }

    fun addOnStatsUpdateListener(onStatsUpdateListener: TrackingServiceInteractor.OnStatsUpdateListener) {

        onStatsUpdateListeners.add(onStatsUpdateListener)
    }

    fun removeOnStatsUpdateListener(onStatsUpdateListener: TrackingServiceInteractor.OnStatsUpdateListener) {

        onStatsUpdateListeners.remove(onStatsUpdateListener)
    }

    fun isInProgress(): Boolean {

        return savedRoute.isNotEmpty() && lastStats != null
    }
}
