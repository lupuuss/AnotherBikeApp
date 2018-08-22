package ga.lupuss.anotherbikeapp.models.dataclass

import ga.lupuss.anotherbikeapp.WeatherIcon
import ga.lupuss.anotherbikeapp.models.weather.pojo.RawCurrentWeatherData
import ga.lupuss.anotherbikeapp.models.weather.pojo.RawForecastData
import java.util.*
import kotlin.math.max
import kotlin.math.min

class WeatherData(
        forecastData: RawForecastData,
        currentWeatherData: RawCurrentWeatherData,
        locale: Locale,
        iconMapper: (String) -> WeatherIcon,
        @Suppress("unused") val downloadTime: Long
) {

    val forecast = mutableListOf<WeatherUnit>()
    val location: String?
    val lat: Double
    val lng: Double
    val daysInfo = mutableListOf<DayInfo>()

    init {

        fillForecast(currentWeatherData, forecastData, locale, iconMapper)

        lat = currentWeatherData.coord.lat
        lng = currentWeatherData.coord.lon

        location = if (currentWeatherData.sys.country != null)
            "${currentWeatherData.name}, ${currentWeatherData.sys.country}"
        else
            null

        fillDaysInfo()
    }

    private fun fillForecast(currentWeatherData: RawCurrentWeatherData, forecastData: RawForecastData, locale: Locale, iconMapper: (String) -> WeatherIcon) {
        forecast.add(
                WeatherUnit(
                        time = Calendar.getInstance(locale).apply { timeInMillis = currentWeatherData.dt * 1000 }, // seconds to ms
                        pressure = currentWeatherData.main.pressure,
                        temperature = currentWeatherData.main.temp,
                        humidity = currentWeatherData.main.humidity,
                        windSpeed = currentWeatherData.wind.speed,
                        windDeg = currentWeatherData.wind.deg,
                        clouds = currentWeatherData.clouds.all,
                        rainVolume = currentWeatherData.rain?.h ?: 0.0,
                        icon = iconMapper.invoke(currentWeatherData.weather.first().icon),
                        description = currentWeatherData.weather.first().description
                )
        )

        forecastData.list.forEach {

            forecast.add(
                    WeatherUnit(time = Calendar.getInstance(locale).apply { timeInMillis = it.dt * 1000 }, // seconds to ms
                            pressure = it.main.pressure,
                            temperature = it.main.temp,
                            humidity = it.main.humidity,
                            windSpeed = it.wind.speed,
                            windDeg = it.wind.deg,
                            clouds = it.clouds.all,
                            rainVolume = it.rain?.h ?: 0.0,
                            icon = iconMapper.invoke(it.weather.first().icon),
                            description = it.weather.first().description)
            )
        }

    }

    private fun fillDaysInfo() {

        var lastDayOfWeek = forecast[0].time.get(Calendar.DAY_OF_WEEK)
        var tempMin: Double? = null
        var tempMax: Double? = null
        var startIndex = 0
        val iconsCounter = mutableMapOf<WeatherIcon, Float>()

        forecast.forEachIndexed { index, it ->

            if (it.time.get(Calendar.DAY_OF_WEEK) != lastDayOfWeek) {

                daysInfo.add(DayInfo(
                        startIndex = startIndex,
                        minTemp = tempMin!!,
                        maxTemp = tempMax!!,
                        dayOfWeek = lastDayOfWeek,
                        icon = iconsCounter.maxBy {
                            it.value
                        }!!.key
                ))

                tempMin = null
                tempMax = null
                lastDayOfWeek = it.time.get(Calendar.DAY_OF_WEEK)
                startIndex = index

                iconsCounter.clear()

            }


            val icon = WeatherIcon.valueOf(
                    it.icon.name.replaceAfterLast("_", "D")
            )
            val count = iconsCounter[icon]

            if (count == null) {

                iconsCounter[icon] = icon.rank

            } else {

                iconsCounter[icon] = count + icon.rank
            }

            tempMin = if(tempMin == null) {

                it.temperature

            } else {

                min(it.temperature, tempMin!!)
            }

            tempMax = if (tempMax == null) {

                it.temperature
            } else {

                max(it.temperature, tempMax!!)
            }
        }
    }

    data class WeatherUnit(
            val time: Calendar,
            val pressure: Double,
            val temperature: Double,
            val humidity: Int,
            val windSpeed: Double,
            val windDeg: Double,
            val clouds: Int,
            val rainVolume: Double,
            val icon: WeatherIcon,
            val description: String
    )

    data class DayInfo(
            val startIndex: Int,
            val icon: WeatherIcon,
            val minTemp: Double,
            val maxTemp: Double,
            val dayOfWeek: Int
    )
}