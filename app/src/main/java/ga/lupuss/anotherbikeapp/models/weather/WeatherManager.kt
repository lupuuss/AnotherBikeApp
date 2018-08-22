package ga.lupuss.anotherbikeapp.models.weather

import ga.lupuss.anotherbikeapp.models.dataclass.WeatherData
import ga.lupuss.anotherbikeapp.models.weather.pojo.RawCurrentWeatherData
import ga.lupuss.anotherbikeapp.models.weather.pojo.RawForecastData
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.util.*

class WeatherManager(
        private val weatherApi: WeatherApi,
        private val timeProvider: () -> Long,
        private val locale: Locale,
        private val backScheduler: Scheduler = Schedulers.io(),
        private val frontScheduler: Scheduler = AndroidSchedulers.mainThread()
) {

    interface OnNewWeatherListener {
        fun onNewWeatherData(weatherData: WeatherData)
    }

    interface OnWeatherRefreshFailureListener {

        fun onWeatherRefreshFailure(exception: Exception?)
    }

    private val weatherListeners: MutableList<OnNewWeatherListener> = mutableListOf()
    private val refreshFailure: MutableList<OnWeatherRefreshFailureListener> = mutableListOf()

    var lastWeatherData: WeatherData? = null
        private set

    fun refreshWeatherData(lat: Double, lng: Double, onWeatherRefreshFailureListener: OnWeatherRefreshFailureListener) {

        refreshFailure.add(onWeatherRefreshFailureListener)

        var forecast: RawForecastData? = null

        weatherApi
                .getWeatherForecastForCoords(lat, lng)
                .observeOn(frontScheduler)
                .subscribeOn(backScheduler)
                .onErrorResumeNext{ Single.error<RawForecastData>(it) }
                .flatMap {
                    forecast = it
                    weatherApi
                            .getCurrentWeatherForCoords(lat, lng)
                            .subscribeOn(backScheduler)
                            .observeOn(frontScheduler)
                }.subscribe { rawData, exception ->

                    exception?.let {

                        onFailure(exception)
                    }

                    if (rawData != null && forecast != null) {

                        onWeatherDataCompleted(forecast!!, rawData)
                    }
                }
    }

    fun removeOnWeatherRefreshFailureListener(onWeatherRefreshFailureListener: OnWeatherRefreshFailureListener) {

        refreshFailure.remove(onWeatherRefreshFailureListener)
    }

    fun removeOnNewWeatherListener(onNewWeatherListener: OnNewWeatherListener) {

        weatherListeners.remove(onNewWeatherListener)
    }

    fun addOnNewWeatherListener(onNewWeatherListener: OnNewWeatherListener) {

        weatherListeners.add(onNewWeatherListener)
    }

    private fun onFailure(t: Throwable?) {

        Timber.d("Weather data fail: $t")

        refreshFailure.forEach {
            it.onWeatherRefreshFailure(t as Exception?)
        }

        refreshFailure.clear()
    }

    private fun onWeatherDataCompleted(forecastData :RawForecastData, currentWeatherData: RawCurrentWeatherData) {

        refreshFailure.clear()

        lastWeatherData = WeatherData(forecastData,currentWeatherData, locale, timeProvider.invoke())
        weatherListeners.forEach {
            it.onNewWeatherData(lastWeatherData!!)
        }
    }
}