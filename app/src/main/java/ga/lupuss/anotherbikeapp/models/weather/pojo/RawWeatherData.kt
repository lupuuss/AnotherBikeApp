package ga.lupuss.anotherbikeapp.models.weather.pojo

import com.google.gson.annotations.SerializedName

data class RawWeatherData(
        var list: List<WeatherUnit>,
        var city: City
) {
    data class City(
            var id: Int,
            var name: String,
            var coord: Coord,
            var country: String,
            var population: Int
    ) {
        data class Coord(
                var lat: Double,
                var lon: Double
        )
    }

    data class WeatherUnit(
            var dt: Long,
            var main: Main,
            var weather: List<Weather>,
            var clouds: Clouds,
            var wind: Wind,
            var sys: Sys,
            @SerializedName("dt_txt")
            var dtTxt: String,
            var rain: Rain?
    ) {
        data class Main(
                var temp: Double,
                @SerializedName("temp_min")
                var tempMin: Double,
                @SerializedName("temp_max")
                var tempMax: Double,
                var pressure: Double,
                @SerializedName("sea_level")
                var seaLevel: Double,
                @SerializedName("grnd_level")
                var grndLevel: Double,
                var humidity: Int
        )

        data class Weather(
                var id: Int,
                var main: String,
                var description: String,
                var icon: String
        )

        data class Rain(
                @SerializedName("3h")
                var h: Double
        )

        data class Wind(
                var speed: Double,
                var deg: Double
        )

        data class Clouds(
                var all: Int
        )

        data class Sys(
                var pod: String
        )
    }
}