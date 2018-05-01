package ga.lupuss.anotherbikeapp.models.trackingservice

import android.Manifest
import android.app.Service
import android.content.Intent
import android.location.Location
import android.os.*
import android.support.v4.content.ContextCompat
import android.support.v4.content.PermissionChecker
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import ga.lupuss.anotherbikeapp.AnotherBikeApp
import ga.lupuss.anotherbikeapp.models.trackingservice.statisticsmanager.StatisticsManager
import ga.lupuss.anotherbikeapp.models.trackingservice.statisticsmanager.statistics.Statistic
import timber.log.Timber
import javax.inject.Inject


/** Receives location from GPS and shares saved route and statistics.
 *  To control service use [TrackingService.ServiceBinder].
 *  To receive location changes implement [TrackingService.LocationDataReceiver].
 *  To receive statistics updates implement [TrackingService.OnStatsUpdateListener]
 */
class TrackingService : Service() {

    @Inject
    lateinit var statsManager: StatisticsManager
    @Inject
    lateinit var locationClient: FusedLocationProviderClient

    val savedRoute
        get() = statsManager.savedRoute

    val lastStats: Map<Statistic.Name, Statistic>?
        get() = statsManager.lastStats

    val routeData
        get() = statsManager.routeData

    var lastLocationAvailability = false

    private val locationDataReceivers = mutableListOf<LocationDataReceiver>()
    private val onStatsUpdateListeners = mutableListOf<OnStatsUpdateListener>()

    private val thread = HandlerThread("Service HandleThread")
    private lateinit var backgroundThread: Handler
    private lateinit var uiThread: Handler

    private var locationRequested = false
    private val locationCallback: LocationCallback = object : LocationCallback() {

        override fun onLocationResult(locRes: LocationResult?) {

            super.onLocationResult(locRes)

            backgroundThread.post {

                locRes ?: return@post

                var location: Location? = null

                for (loc in locRes.locations) {
                    location = loc
                    break
                }

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

    interface LocationDataReceiver {

        fun onNewLocation(points: List<LatLng>)
        fun onLocationAvailability(available: Boolean)
    }

    interface OnStatsUpdateListener {
        fun onStatsUpdate(stats: Map<Statistic.Name, Statistic>)
    }

    inner class ServiceBinder : Binder() {

        val lastLocationAvailability get() = this@TrackingService.lastLocationAvailability

        val savedRoute get() = this@TrackingService.savedRoute

        val lastStats get() = this@TrackingService.lastStats

        val routeData get() = this@TrackingService.routeData

        fun isServiceInProgress(): Boolean = this@TrackingService.isInProgress()

        fun connectServiceDataReceiver(locationDataReceiver: LocationDataReceiver) =
                this@TrackingService.connectLocationDataReceiver(locationDataReceiver)

        fun disconnectServiceDataReceiver(locationDataReceiver: LocationDataReceiver) =
                this@TrackingService.disconnectLocationDataReceiver(locationDataReceiver)

        fun addOnStatsUpdateListener(onStatsUpdateListener: OnStatsUpdateListener) =
                this@TrackingService.addOnStatsUpdateListener(onStatsUpdateListener)

        fun removeOnStatsUpdateListener(onStatsUpdateListener: OnStatsUpdateListener) =
                this@TrackingService.removeOnStatsUpdateListener(onStatsUpdateListener)
    }

    override fun onCreate() {
        super.onCreate()

        DaggerTrackingServiceComponent.builder()
                .anotherBikeAppComponent(AnotherBikeApp.get(this.application).mainComponent)
                .build()
                .inject(this)

        uiThread = Handler(mainLooper)
        thread.start()
        backgroundThread = Handler(thread.looper)

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

        val permissionStatus =
                ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                )

        if (permissionStatus == PermissionChecker.PERMISSION_GRANTED) {

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

    fun connectLocationDataReceiver(locationDataReceiver: LocationDataReceiver) {

        locationDataReceivers.add(locationDataReceiver)
    }

    fun disconnectLocationDataReceiver(locationDataReceiver: LocationDataReceiver) {

        locationDataReceivers.remove(locationDataReceiver)
    }

    fun addOnStatsUpdateListener(onStatsUpdateListener: OnStatsUpdateListener) {

        onStatsUpdateListeners.add(onStatsUpdateListener)
    }

    fun removeOnStatsUpdateListener(onStatsUpdateListener: OnStatsUpdateListener) {

        onStatsUpdateListeners.remove(onStatsUpdateListener)
    }

    fun isInProgress(): Boolean {

        return savedRoute.isNotEmpty() && lastStats != null
    }
}