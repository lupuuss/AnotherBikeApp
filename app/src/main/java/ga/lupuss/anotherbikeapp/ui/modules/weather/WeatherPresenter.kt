package ga.lupuss.anotherbikeapp.ui.modules.weather

import com.google.android.gms.maps.model.LatLng
import ga.lupuss.anotherbikeapp.Message
import ga.lupuss.anotherbikeapp.base.Presenter
import ga.lupuss.anotherbikeapp.models.dataclass.WeatherData
import ga.lupuss.anotherbikeapp.models.weather.WeatherManager
import java.util.*
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

        fun refresh(latLng: LatLng?) {

            if (latLng != null) {

                weatherManager.refreshWeatherData(latLng.latitude, latLng.longitude, this)

            } else {

                view.postMessage(Message.LOCATION_NOT_AVAILABLE)
            }
        }

        if (view.checkLocationPermission()) {

            refresh(view.getLastKnownLocation())
            return
        }

        view.requestLocationPermission {

            if (it) {

                refresh(view.getLastKnownLocation())

            } else {

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