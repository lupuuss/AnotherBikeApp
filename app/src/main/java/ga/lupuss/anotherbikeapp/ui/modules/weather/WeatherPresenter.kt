package ga.lupuss.anotherbikeapp.ui.modules.weather

import ga.lupuss.anotherbikeapp.kotlin.Resettable
import ga.lupuss.anotherbikeapp.kotlin.ResettableManager
import ga.lupuss.anotherbikeapp.models.weather.WeatherApi
import ga.lupuss.anotherbikeapp.models.weather.pojo.RawWeatherForecastData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import javax.inject.Inject

class WeatherPresenter @Inject constructor(private val weatherApi: WeatherApi,
                                           weatherView: WeatherView) {
    private val resettableManager = ResettableManager()
    var weatherView: WeatherView by Resettable(resettableManager)

    init {
        this.weatherView = weatherView
    }

    fun requestWeatherData() {

        weatherApi.getWeatherForecastForCoords(0.0, 0.0).enqueue(object : Callback<RawWeatherForecastData> {
            override fun onFailure(call: Call<RawWeatherForecastData>?, t: Throwable?) {

            }

            override fun onResponse(call: Call<RawWeatherForecastData>?, response: Response<RawWeatherForecastData>?) {

                weatherView.updateWeather(response!!.body()!!)
                weatherView.loadWeatherImage(response.body()!!.list.first().weather.first().icon)
                Timber.d("http://openweathermap.org/img/w/${response.body()!!.list.first().weather.first().icon}.png")
            }
        })
    }

    fun onDestroyView() {

        resettableManager.reset()
    }
}