package ga.lupuss.anotherbikeapp.models.trackingservice

import android.Manifest
import android.app.Notification
import android.app.Service
import android.content.Intent
import android.location.Location
import android.os.*
import com.google.android.gms.location.*
import ga.lupuss.anotherbikeapp.AnotherBikeApp
import ga.lupuss.anotherbikeapp.AppUnit
import ga.lupuss.anotherbikeapp.models.base.TrackingServiceInteractor
import ga.lupuss.anotherbikeapp.models.trackingservice.statisticsmanager.StatisticsManager
import ga.lupuss.anotherbikeapp.models.dataclass.Statistic
import ga.lupuss.anotherbikeapp.models.base.PreferencesInteractor
import ga.lupuss.anotherbikeapp.models.dataclass.RoutePhoto
import ga.lupuss.anotherbikeapp.ui.extensions.checkPermission
import timber.log.Timber
import javax.inject.Inject


/** Receives location from GPS and shares saved route and statistics.
 *  To control service use [TrackingServiceInteractor].
 *  To receive location changes implement [TrackingServiceInteractor.LocationDataReceiver].
 *  To receive statistics updates implement [TrackingServiceInteractor.OnStatsUpdateListener]
 */
class TrackingService : Service(), PreferencesInteractor.OnTrackingUnitChangedListener {

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

                Timber.v ("NEW POINT = [${location.latitude}, ${location.longitude}]")
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

            Timber.v("Location availability: $isLocationOk")
            locationDataReceivers.forEach { it.onLocationAvailability(isLocationOk) }
        }

    }

    inner class ServiceBinder : Binder(), TrackingServiceInteractor {

        private var isNotificationInitialized = false

        override val lastLocationAvailability get() = this@TrackingService.lastLocationAvailability

        override val savedRoute get() = this@TrackingService.savedRoute

        override val lastStats get() = this@TrackingService.lastStats

        override val routeData get() = this@TrackingService.routeData

        override val photosCount: Int
            get() = statsManager.photosCount

        override fun getPhoto(position: Int): RoutePhoto {

            return statsManager.getPhoto(position)
        }

        override fun addPhoto(photo: RoutePhoto) {

            statsManager.addPhoto(photo)
        }

        override fun removePhotoAt(position: Int) {

            statsManager.removePhotoAt(position)
        }

        override fun isServiceInProgress(): Boolean = this@TrackingService.isInProgress()

        override fun connectServiceDataReceiver(locationDataReceiver: TrackingServiceInteractor.LocationDataReceiver) =
                this@TrackingService.connectLocationDataReceiver(locationDataReceiver)

        override fun disconnectServiceDataReceiver(locationDataReceiver: TrackingServiceInteractor.LocationDataReceiver) =
                this@TrackingService.disconnectLocationDataReceiver(locationDataReceiver)

        override fun addOnStatsUpdateListener(onStatsUpdateListener: TrackingServiceInteractor.OnStatsUpdateListener) =
                this@TrackingService.addOnStatsUpdateListener(onStatsUpdateListener)

        override fun removeOnStatsUpdateListener(onStatsUpdateListener: TrackingServiceInteractor.OnStatsUpdateListener) =
                this@TrackingService.removeOnStatsUpdateListener(onStatsUpdateListener)

        fun initNotification(id: Int, notification: Notification) {
            this@TrackingService.startForeground(id, notification)
            isNotificationInitialized = true
        }
    }

    override fun onTrackingUnitChanged(speedUnit: AppUnit.Speed, distanceUnit: AppUnit.Distance) {

        statsManager.speedUnit = speedUnit
        statsManager.distanceUnit = distanceUnit
        statsManager.refresh()
    }

    override fun onCreate() {
        super.onCreate()

        DaggerTrackingServiceComponent.builder()
                .userComponent(AnotherBikeApp.get(this.application).userComponent!!)
                .build()
                .inject(this)

        uiThread = Handler(mainLooper)
        thread.start()
        backgroundThread = Handler(thread.looper)

        preferencesInteractor.addOnTrackingUnitChangedListener(this, this)

        statsManager.onNewStats = { stats ->

            uiThread.post {

                onStatsUpdateListeners.forEach { it.onStatsUpdate(stats) }
            }
        }

        Timber.v("Service started")
    }

    override fun onBind(p0: Intent?): IBinder? {

        Timber.v("Bound $p0")

        if (!locationRequested) {

            requestLocation()
        }
        return ServiceBinder()
    }

    override fun onUnbind(intent: Intent?): Boolean {

        Timber.v("Unbound > $intent")
        onStatsUpdateListeners.clear()
        locationDataReceivers.clear()
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

            Timber.v("Location request send")
        }

        locationRequested = true
    }

    override fun onDestroy() {
        super.onDestroy()

        onStatsUpdateListeners.clear()
        locationDataReceivers.clear()
        locationClient.removeLocationUpdates(locationCallback)
        statsManager.timer.stop()
        thread.quit()
        Timber.v("Service destroyed.")
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
