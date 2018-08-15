package ga.lupuss.anotherbikeapp.models.dataclass

import ga.lupuss.anotherbikeapp.models.weather.pojo.RawWeatherData

class WeatherData(
        weatherData: RawWeatherData,
        val downloadTime: Long
) {

    val forecast = mutableListOf<WeatherUnit>()

    init {

        weatherData.list.forEach {
            forecast.add(
                    WeatherUnit(time = it.dt,
                            pressure = it.main.pressure,
                            temperature = it.main.temp,
                            humidity = it.main.humidity,
                            windSpeed = it.wind.speed,
                            windDeg = it.wind.deg,
                            clouds = it.clouds.all,
                            rainVolume = it.rain?.h ?: 0.0,
                            iconName = it.weather.first().icon,
                            description = it.weather.first().description)
            )
        }
    }

    data class WeatherUnit(
            val time: Long,
            val pressure: Double,
            val temperature: Double,
            val humidity: Int,
            val windSpeed: Double,
            val windDeg: Double,
            val clouds: Int,
            val rainVolume: Double,
            val iconName: String,
            val description: String
    )
}