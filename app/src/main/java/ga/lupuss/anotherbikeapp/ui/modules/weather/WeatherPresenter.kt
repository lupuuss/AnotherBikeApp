package ga.lupuss.anotherbikeapp.ui.modules.weather

import ga.lupuss.anotherbikeapp.Message
import ga.lupuss.anotherbikeapp.base.Presenter
import ga.lupuss.anotherbikeapp.models.base.WeatherManager
import ga.lupuss.anotherbikeapp.models.dataclass.WeatherData
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
            view.setWeather(it)
        }

        weatherManager.addOnNewWeatherListener(this)
        refreshWeatherManager()
    }

    private fun refreshWeatherManager() {

        view.isRefreshButtonVisible = false
        view.isRefreshProgressBarVisible = true

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

        view.setWeather(weatherData)
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

        refreshWeatherManager()
    }

    fun onClickLocationInfo() {

        weatherManager.lastWeatherData?.let {
            view.redirectToGoogleMaps(it.lat, it.lng, it.location)
        }
    }

    fun onWeatherSeekBarPositionChanged(position: Int) {

        weatherManager.lastWeatherData?.let {

            view.updateWeather(it, position, currentDay, currentDay)
        }
    }

    fun onClickWeatherDay(day: Int) {

        val dayBefore = currentDay
        currentDay = day
        view.updateWeather(weatherManager.lastWeatherData!!, 0, currentDay, dayBefore)
        view.resetSeekBar()
    }

    override fun notifyOnDestroy(isFinishing: Boolean) {
        weatherManager.removeOnNewWeatherListener(this)
        weatherManager.removeOnWeatherRefreshFailureListener(this)
        super.notifyOnDestroy(isFinishing)
    }
}