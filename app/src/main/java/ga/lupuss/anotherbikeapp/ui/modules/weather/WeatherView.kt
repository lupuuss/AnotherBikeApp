package ga.lupuss.anotherbikeapp.ui.modules.weather

import android.annotation.SuppressLint
import ga.lupuss.anotherbikeapp.base.BaseView
import ga.lupuss.anotherbikeapp.models.dataclass.WeatherData

interface WeatherView : BaseView {
    var isRefreshButtonVisible: Boolean
    var isRefreshProgressBarVisible: Boolean

    fun redirectToGoogleMaps(lat: Double, lng: Double, name: String?)
    fun resetSeekBar()

    fun updateWeather(data: WeatherData, position: Int, currentDay: Int, dayBefore: Int)
    fun setWeather(data: WeatherData)
}