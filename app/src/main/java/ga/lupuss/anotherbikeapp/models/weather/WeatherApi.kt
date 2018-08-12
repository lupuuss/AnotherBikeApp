package ga.lupuss.anotherbikeapp.models.weather

import ga.lupuss.anotherbikeapp.models.weather.pojo.RawWeatherForecastData
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    @GET("forecast")
    fun getWeatherForecastForCoords(@Query("lat")lat: Double, @Query("lon")lng: Double): Call<RawWeatherForecastData>
}