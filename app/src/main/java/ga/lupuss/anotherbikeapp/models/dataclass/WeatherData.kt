package ga.lupuss.anotherbikeapp.models.dataclass

import ga.lupuss.anotherbikeapp.models.weather.pojo.RawCurrentWeatherData
import ga.lupuss.anotherbikeapp.models.weather.pojo.RawForecastData
import timber.log.Timber
import java.util.*
import kotlin.math.max
import kotlin.math.min

class WeatherData(
        forecastData: RawForecastData,
        currentWeatherData: RawCurrentWeatherData,
        locale: Locale,
        val downloadTime: Long
) {

    val forecast = mutableListOf<WeatherUnit>()
    val location: String?
    val lat: Double
    val lng: Double
    val daysInfo = mutableListOf<DayInfo>()

    init {



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
                        iconName = currentWeatherData.weather.first().icon,
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
                            iconName = it.weather.first().icon,
                            description = it.weather.first().description)
            )
        }

        lat = currentWeatherData.coord.lat
        lng = currentWeatherData.coord.lon

        location = if (currentWeatherData.sys.country != null)
            "${currentWeatherData.name}, ${currentWeatherData.sys.country}"
        else
            null

        var lastDayOfWeek = forecast[0].time.get(Calendar.DAY_OF_WEEK)
        var tempMin: Double? = null
        var tempMax: Double? = null
        var startIndex = 0
        val iconsCounter = mutableMapOf<String, Int>()

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


            val iconName = it.iconName.replace("n", "d")
            val count = iconsCounter[iconName]

            if (count == null) {

                iconsCounter[iconName] = 1

            } else {

                iconsCounter[iconName] = count + 1
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

        Timber.d(daysInfo.toString())
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
            val iconName: String,
            val description: String
    )

    data class DayInfo(
            val startIndex: Int,
            val icon: String,
            val minTemp: Double,
            val maxTemp: Double,
            val dayOfWeek: Int
    )
}