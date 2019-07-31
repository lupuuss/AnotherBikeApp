package ga.lupuss.anotherbikeapp.models.base

import ga.lupuss.anotherbikeapp.models.dataclass.WeatherData

abstract class  WeatherManager {

    interface OnNewWeatherListener {
        fun onNewWeatherData(weatherData: WeatherData)
    }

    interface OnWeatherRefreshFailureListener {

        fun onWeatherRefreshFailure(exception: Exception?)
    }

    abstract val lastWeatherData: WeatherData?

    abstract fun refreshWeatherData(lat: Double, lng: Double, onWeatherRefreshFailureListener: OnWeatherRefreshFailureListener)

    protected val weatherListeners: MutableList<OnNewWeatherListener> = mutableListOf()

    protected val refreshFailure: MutableList<OnWeatherRefreshFailureListener> = mutableListOf()

    fun addOnNewWeatherListener(onNewWeatherListener: OnNewWeatherListener) {

        weatherListeners.add(onNewWeatherListener)
    }

    fun removeOnNewWeatherListener(onNewWeatherListener: OnNewWeatherListener) {

        weatherListeners.remove(onNewWeatherListener)
    }

    fun removeOnWeatherRefreshFailureListener(onWeatherRefreshFailureListener: OnWeatherRefreshFailureListener) {

        refreshFailure.remove(onWeatherRefreshFailureListener)
    }
}