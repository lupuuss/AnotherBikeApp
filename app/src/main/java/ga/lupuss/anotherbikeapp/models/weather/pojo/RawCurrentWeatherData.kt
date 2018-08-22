package ga.lupuss.anotherbikeapp.models.weather.pojo

import com.google.gson.annotations.SerializedName

data class RawCurrentWeatherData(
        var coord: Coord,
        var weather: List<Weather>,
        var main: Main,
        var wind: Wind,
        var clouds: Clouds,
        var rain: Rain?,
        var dt: Long,
        var sys: Sys,
        var id: Int,
        var name: String
) {
    data class Sys(
            var country: String?,
            var sunrise: Long,
            var sunset: Long
    )

    data class Coord(
            var lon: Double,
            var lat: Double
    )

    data class Weather(
            var id: Int,
            var main: String,
            var description: String,
            var icon: String
    )

    data class Wind(
            var speed: Double,
            var deg: Double
    )

    data class Rain(
            @SerializedName("3h")
            var h: Double
    )

    data class Main(
            var temp: Double,
            var pressure: Double,
            var humidity: Int,
            @SerializedName("temp_min")
            var tempMin: Double,
            @SerializedName("temp_max")
            var tempMax: Double
    )

    data class Clouds(
            var all: Int
    )
}