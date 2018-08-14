package ga.lupuss.anotherbikeapp.ui.modules.weather

import ga.lupuss.anotherbikeapp.models.weather.pojo.RawWeatherForecastData

interface WeatherView {

    fun loadWeatherImage(name: String)
    fun updateWeather(data: RawWeatherForecastData)
}