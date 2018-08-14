package ga.lupuss.anotherbikeapp.models.weather

import ga.lupuss.anotherbikeapp.models.dataclass.WeatherData
import ga.lupuss.anotherbikeapp.models.weather.pojo.RawWeatherData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber

class WeatherManager(private val weatherApi: WeatherApi, private val timeProvider: () -> Long): Callback<RawWeatherData> {

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
        weatherApi.getWeatherForecastForCoords(lat, lng).enqueue(this)
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

    override fun onFailure(call: Call<RawWeatherData>?, t: Throwable?) {

        Timber.d("Weather data fail: $t")

        refreshFailure.forEach {
            it.onWeatherRefreshFailure(t as Exception?)
        }

        refreshFailure.clear()
    }

    override fun onResponse(call: Call<RawWeatherData>?, response: Response<RawWeatherData>?) {

        Timber.d("Server respond!")

        if (response?.body() != null) {

            Timber.d("Good result! -> ${response.body()}")

            refreshFailure.clear()

            lastWeatherData = WeatherData(response.body()!!, timeProvider.invoke())
            weatherListeners.forEach {
                it.onNewWeatherData(lastWeatherData!!)
            }

        } else {

            onFailure(null, Exception())
        }
    }
}