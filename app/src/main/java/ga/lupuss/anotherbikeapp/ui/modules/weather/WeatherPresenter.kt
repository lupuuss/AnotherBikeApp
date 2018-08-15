package ga.lupuss.anotherbikeapp.ui.modules.weather

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
        weatherManager.refreshWeatherData(52.173040, 20.268581, this)
    }

    override fun onNewWeatherData(weatherData: WeatherData) {

        view.updateWeather(weatherData)
    }

    override fun onWeatherRefreshFailure(exception: Exception?) {

        exception?.let {
            view.makeToast(exception.toString())
        }
    }

    fun onRefreshButtonClick() {

        weatherManager.refreshWeatherData(Random().nextDouble() * 90.0, Random().nextDouble() * 180.0, this)
    }

    override fun notifyOnDestroy(isFinishing: Boolean) {
        weatherManager.removeOnNewWeatherListener(this)
        weatherManager.removeOnWeatherRefreshFailureListener(this)
        super.notifyOnDestroy(isFinishing)
    }

}