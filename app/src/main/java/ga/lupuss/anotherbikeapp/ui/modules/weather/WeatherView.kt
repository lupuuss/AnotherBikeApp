package ga.lupuss.anotherbikeapp.ui.modules.weather

import ga.lupuss.anotherbikeapp.base.BaseView
import ga.lupuss.anotherbikeapp.models.dataclass.WeatherData

interface WeatherView : BaseView {
    var isRefreshButtonVisible: Boolean
    var isRefreshProgressBarVisible: Boolean

    fun updateWeather(data: WeatherData)
    fun redirectToGoogleMaps(lat: Double, lng: Double, name: String?)
}