package ga.lupuss.anotherbikeapp.ui.modules.weather

import ga.lupuss.anotherbikeapp.AppUnit
import ga.lupuss.anotherbikeapp.base.BaseView
import ga.lupuss.anotherbikeapp.base.LabeledView
import ga.lupuss.anotherbikeapp.models.dataclass.WeatherData

interface WeatherView : BaseView, LabeledView {

    fun redirectToGoogleMaps(lat: Double, lng: Double, name: String?)
    fun resetSeekBar()

    fun updateWeather(data: WeatherData, position: Int, currentDay: Int, dayBefore: Int)
    fun setWeather(data: WeatherData)
    fun refreshUnits(windSpeedUnit: AppUnit.Speed, temperatureUnit: AppUnit.Temperature)
}