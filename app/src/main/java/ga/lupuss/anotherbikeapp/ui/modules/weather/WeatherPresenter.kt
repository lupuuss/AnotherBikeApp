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

    private var currentDay = 0

    override fun notifyOnViewReady() {

        weatherManager.lastWeatherData?.let {
            view.setWeather(it, 0, 0)
        }

        weatherManager.addOnNewWeatherListener(this)
        refreshWeatherManager()
    }

    private fun refreshWeatherManager() {

        view.provideLocationPermission(
                onLocationPermissionGranted = {

                    view.requestSingleLocationUpdate { ok, location ->

                        if (ok && location != null) {

                            weatherManager.refreshWeatherData(location.latitude, location.longitude, this)
                        } else {

                            onWeatherRefreshFailure(null)
                            view.postMessage(Message.LOCATION_NOT_AVAILABLE)
                        }
                    }
                },
                onLocationPermissionRefused = {
                    onWeatherRefreshFailure(null)
                    view.postMessage(Message.LOCATION_NOT_AVAILABLE)
                })
    }

    override fun onNewWeatherData(weatherData: WeatherData) {

        view.resetSeekBar()

        view.setWeather(weatherData, 0, 0)
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

    fun onWeatherSeekBarPositionChanged(position: Int) {

        weatherManager.lastWeatherData?.let {

            view.setWeather(it, position, currentDay)
        }
    }

    fun onClickWeatherDay(day: Int) {

        currentDay = day
        view.setWeather(weatherManager.lastWeatherData!!, 0, currentDay)
        view.resetSeekBar()
    }

    override fun notifyOnDestroy(isFinishing: Boolean) {
        weatherManager.removeOnNewWeatherListener(this)
        weatherManager.removeOnWeatherRefreshFailureListener(this)
        super.notifyOnDestroy(isFinishing)
    }
}