package ga.lupuss.anotherbikeapp.models.weather

import ga.lupuss.anotherbikeapp.kotlin.SchedulersPackage
import ga.lupuss.anotherbikeapp.models.base.WeatherManager
import ga.lupuss.anotherbikeapp.models.dataclass.WeatherData
import ga.lupuss.anotherbikeapp.models.weather.pojo.RawCurrentWeatherData
import ga.lupuss.anotherbikeapp.models.weather.pojo.RawForecastData
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.util.*

class OpenWeatherManager(
        private val weatherApi: OpenWeatherApi,
        private val timeProvider: () -> Long,
        private val locale: Locale,
        private val schedulersPackage: SchedulersPackage
) : WeatherManager() {


    override var lastWeatherData: WeatherData? = null
        private set

    override fun refreshWeatherData(lat: Double, lng: Double, onWeatherRefreshFailureListener: WeatherManager.OnWeatherRefreshFailureListener) {

        refreshFailure.add(onWeatherRefreshFailureListener)

        var forecast: RawForecastData? = null

        weatherApi
                .getWeatherForecastForCoords(lat, lng)
                .observeOn(schedulersPackage.frontScheduler)
                .subscribeOn(schedulersPackage.backScheduler)
                .onErrorResumeNext{ Single.error<RawForecastData>(it) }
                .flatMap {
                    forecast = it
                    weatherApi
                            .getCurrentWeatherForCoords(lat, lng)
                            .subscribeOn(schedulersPackage.backScheduler)
                            .observeOn(schedulersPackage.frontScheduler)
                }.subscribe { rawData, exception ->

                    exception?.let {

                        onFailure(exception)
                    }

                    if (rawData != null && forecast != null) {

                        onWeatherDataCompleted(forecast!!, rawData)
                    }
                }
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