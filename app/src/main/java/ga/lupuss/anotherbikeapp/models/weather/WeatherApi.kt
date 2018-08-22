package ga.lupuss.anotherbikeapp.models.weather

import ga.lupuss.anotherbikeapp.models.weather.pojo.RawCurrentWeatherData
import ga.lupuss.anotherbikeapp.models.weather.pojo.RawForecastData
import io.reactivex.Single
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherApi {

    @GET("forecast")
    fun getWeatherForecastForCoords(@Query("lat")lat: Double, @Query("lon")lng: Double): Single<RawForecastData>

    @GET("weather")
    fun getCurrentWeatherForCoords(@Query("lat")lat: Double, @Query("lon")lng: Double): Single<RawCurrentWeatherData>
}