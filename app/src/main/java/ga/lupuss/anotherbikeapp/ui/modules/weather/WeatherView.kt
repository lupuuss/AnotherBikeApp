package ga.lupuss.anotherbikeapp.ui.modules.weather

import ga.lupuss.anotherbikeapp.base.BaseView
import ga.lupuss.anotherbikeapp.models.dataclass.WeatherData

interface WeatherView : BaseView {

    fun updateWeather(data: WeatherData)
}