package ga.lupuss.anotherbikeapp.ui.modules.weather

import ga.lupuss.anotherbikeapp.Message
import ga.lupuss.anotherbikeapp.base.Presenter
import ga.lupuss.anotherbikeapp.models.dataclass.WeatherData
import ga.lupuss.anotherbikeapp.models.weather.WeatherManager
import javax.inject.Inject

class WeatherPresenter @Inject constructor(
        private val weatherManager: WeatherManager,
        weatherView: WeatherView
) : Presenter<WeatherView>(), WeatherManager.OnNewWeatherListener, WeatherManager.OnWeatherRefreshFailureListener {

    init {
        this.view = weatherView
    }

    override fun notifyOnViewReady() {

        weatherManager.lastWeatherData?.let {
            view.updateWeather(it)
        }

        weatherManager.addOnNewWeatherListener(this)
        refreshWeatherManager()
    }

    private fun refreshWeatherManager() {

        fun tryToRefresh() {

            view.requestSingleLocationUpdate { ok, location ->

                if (ok) {

                    weatherManager.refreshWeatherData(location.latitude, location.longitude, this)

                } else {

                    onWeatherRefreshFailure(null)
                    view.postMessage(Message.LOCATION_NOT_AVAILABLE)
                }
            }
        }

        if (view.checkLocationPermission()) {

            tryToRefresh()
            return
        }

        view.requestLocationPermission {

            if (it) {

                tryToRefresh()

            } else {

                onWeatherRefreshFailure(null)
                view.postMessage(Message.LOCATION_NOT_AVAILABLE)

            }
        }
    }

    override fun onNewWeatherData(weatherData: WeatherData) {

        view.updateWeather(weatherData)
        view.isRefreshButtonVisible = true
        view.isRefreshProgressBarVisible = false
    }

    override fun onWeatherRefreshFailure(exception: Exception?) {

        exception?.let {
            view.makeToast(exception.toString())
        }
        view.isRefreshButtonVisible = true
        view.isRefreshProgressBarVisible = false
    }

    fun onClickRefreshButton() {

        view.isRefreshButtonVisible = false
        view.isRefreshProgressBarVisible = true
        refreshWeatherManager()
    }

    fun onClickLocationInfo() {

        weatherManager.lastWeatherData?.let {
            view.redirectToGoogleMaps(it.lat, it.lng, it.location)
        }
    }

    override fun notifyOnDestroy(isFinishing: Boolean) {
        weatherManager.removeOnNewWeatherListener(this)
        weatherManager.removeOnWeatherRefreshFailureListener(this)
        super.notifyOnDestroy(isFinishing)
    }
}