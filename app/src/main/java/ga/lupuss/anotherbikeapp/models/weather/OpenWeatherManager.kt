package ga.lupuss.anotherbikeapp.models.weather

import ga.lupuss.anotherbikeapp.WeatherIcon
import ga.lupuss.anotherbikeapp.kotlin.SchedulersPackage
import ga.lupuss.anotherbikeapp.models.base.WeatherManager
import ga.lupuss.anotherbikeapp.models.dataclass.WeatherData
import ga.lupuss.anotherbikeapp.models.weather.pojo.RawCurrentWeatherData
import ga.lupuss.anotherbikeapp.models.weather.pojo.RawForecastData
import io.reactivex.Single
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

    private fun resolveIcon(icon: String): WeatherIcon {

        return when(icon) {

            "01d" -> WeatherIcon.SUNNY_D
            "01n" -> WeatherIcon.SUNNY_N
            "02d" -> WeatherIcon.FEW_CLOUDS_D
            "02n" -> WeatherIcon.FEW_CLOUDS_N
            "03d" -> WeatherIcon.SCATTERED_CLOUDS_D
            "03n" -> WeatherIcon.SCATTERED_CLOUDS_N
            "04d" -> WeatherIcon.BROKEN_CLOUDS_D
            "04n" -> WeatherIcon.BROKEN_CLOUDS_N
            "09d" -> WeatherIcon.SHOWER_RAIN_D
            "09n" -> WeatherIcon.SHOWER_RAIN_N
            "10d" -> WeatherIcon.RAIN_D
            "10n" -> WeatherIcon.RAIN_N
            "11d" -> WeatherIcon.THUNDERSTORM_D
            "11n" -> WeatherIcon.THUNDERSTORM_N
            "13d" -> WeatherIcon.SNOW_D
            "13n" -> WeatherIcon.SNOW_N
            "50d" -> WeatherIcon.MIST_D
            "50n" -> WeatherIcon.MIST_N
            else -> WeatherIcon.EMPTY
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

        lastWeatherData = WeatherData(forecastData,currentWeatherData, locale, { resolveIcon(it) },timeProvider.invoke())
        weatherListeners.forEach {
            it.onNewWeatherData(lastWeatherData!!)
        }
    }
}