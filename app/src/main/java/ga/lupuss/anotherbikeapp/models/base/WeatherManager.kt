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

    protected val weatherListeners: MutableList<WeatherManager.OnNewWeatherListener> = mutableListOf()

    protected val refreshFailure: MutableList<WeatherManager.OnWeatherRefreshFailureListener> = mutableListOf()

    fun addOnNewWeatherListener(onNewWeatherListener: WeatherManager.OnNewWeatherListener) {

        weatherListeners.add(onNewWeatherListener)
    }

    fun removeOnNewWeatherListener(onNewWeatherListener: WeatherManager.OnNewWeatherListener) {

        weatherListeners.remove(onNewWeatherListener)
    }

    fun removeOnWeatherRefreshFailureListener(onWeatherRefreshFailureListener: WeatherManager.OnWeatherRefreshFailureListener) {

        refreshFailure.remove(onWeatherRefreshFailureListener)
    }
}