package ga.lupuss.anotherbikeapp.ui.modules.main

import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import android.view.View
import com.google.gson.Gson
import ga.lupuss.anotherbikeapp.Message
import ga.lupuss.anotherbikeapp.base.Presenter
import ga.lupuss.anotherbikeapp.models.interfaces.AuthInteractor
import ga.lupuss.anotherbikeapp.models.firebase.OnDocumentChanged
import ga.lupuss.anotherbikeapp.models.interfaces.PreferencesInteractor
import ga.lupuss.anotherbikeapp.models.interfaces.RoutesManager
import ga.lupuss.anotherbikeapp.models.trackingservice.TrackingService
import ga.lupuss.anotherbikeapp.models.pojo.Statistic
import ga.lupuss.anotherbikeapp.ui.modules.tracking.TrackingActivity
import timber.log.Timber

import javax.inject.Inject
/**
 * Presenter associated with [MainActivity]. [MainActivity] must implement [MainView].
 */
class MainPresenter @Inject constructor(private val routesManager: RoutesManager,
                                        private val gson: Gson,
                                        private val authInteractor: AuthInteractor,
                                        private val preferencesInteractor: PreferencesInteractor)
    : Presenter, OnDocumentChanged {

    class State(val isServiceActive: Boolean)

    var speedUnit: Statistic.Unit = preferencesInteractor.speedUnit
    var distanceUnit: Statistic.Unit = preferencesInteractor.distanceUnit

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

            mainView.checkLocationPermission() -> startTracking() // Starting new tracking service and tracking activity

            else -> mainView.requestLocationPermission {
                if (it) {

                    startTracking()

                } else {

                    mainView.postMessage(Message.NO_PERMISSION)
                }
            }
        }
    }

    fun onClickSettings() {

        mainView.startSettingsActivity()
    }

    fun onClickSignOut() {

        if (serviceBinder == null || !serviceBinder!!.isServiceInProgress()) {

            signOut()
        } else {
            mainView.showExitWarningDialog({ signOut() })
        }
    }

    private fun signOut() {
        authInteractor.signOut()
        mainView.startLoginActivity()
        mainView.finishActivity()
    }

    fun onHistoryRecyclerItemRequest(position: Int) = routesManager.readShortRouteData(position)

    fun onHistoryRecyclerItemCountRequest(): Int = routesManager.shortRouteDataCount()

    fun notifyRecyclerReachedBottom() {

        onLoadMoreRequest()
    }

    fun onExitRequest() {

        if (serviceBinder == null || !serviceBinder!!.isServiceInProgress()) {

            mainView.finishActivity()

        } else {

            mainView.showExitWarningDialog({ mainView.finishActivity() })
        }
    }

    private fun onLoadMoreRequest() {
        mainView.setProgressBarVisibility(View.VISIBLE)

        routesManager.requestMoreShortRouteData({

            mainView.setProgressBarVisibility(View.GONE)

            if (routesManager.shortRouteDataCount() == 0)
                mainView.setNoDataTextVisibility(View.VISIBLE)

        }, {
            mainView.setProgressBarVisibility(View.GONE)
            Timber.d(it)
        })
    }

    override fun initWithStateJson(stateJson: String?) {

        gson.fromJson<State>(stateJson, State::class.java).let {

            isServiceActive = it.isServiceActive
        }
    }

    override fun notifyOnViewReady() {
        super.notifyOnViewReady()

        preferencesInteractor.addOnUnitChangedListener(
                this,
                object : PreferencesInteractor.OnUnitChangedListener {
                    override fun onUnitChanged(speedUnit: Statistic.Unit, distanceUnit: Statistic.Unit) {

                        this@MainPresenter.speedUnit = speedUnit
                        this@MainPresenter.distanceUnit = distanceUnit

                        mainView.refreshRecyclerAdapter()
                    }

                })

        if (isServiceActive) {

            mainView.bindTrackingService(serviceConnection)
        }

        mainView.setNoDataTextVisibility(View.INVISIBLE)
        routesManager.addRoutesDataChangedListener(this)
        onLoadMoreRequest()
        mainView.setDrawerHeaderInfos(authInteractor.getDisplayName(), authInteractor.getEmail())
    }

    override fun notifyOnResult(requestCode: Int, resultCode: Int) {

        if (requestCode == MainActivity.Request.TRACKING_ACTIVITY_REQUEST) {

            when (resultCode) {

                TrackingActivity.Result.DONE -> {

                    Timber.d("Service done. Data may be saved")

                    val routeData = serviceBinder?.routeData

                    stopTracking()

                    routeData?.let {

                        routesManager.keepTempRoute(it)
                        mainView.startSummaryActivity()
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

    override fun onNewDocument(position: Int) {

        mainView.setNoDataTextVisibility(View.INVISIBLE)
        mainView.notifyRecyclerItemInserted(position)
    }

    override fun onDocumentModified(position: Int) {
        mainView.notifyRecyclerItemChanged(position)
    }

    override fun onDocumentDeleted(position: Int) {
        mainView.notifyRecyclerItemRemoved(position)

        if (routesManager.shortRouteDataCount() == 0) {

            mainView.setNoDataTextVisibility(View.VISIBLE)
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

    override fun getStateJson(): String? = gson.toJson(State(isServiceActive))

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

        routesManager.removeOnRoutesDataChangedListener(this)
    }
}
