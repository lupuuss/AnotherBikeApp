package ga.lupuss.anotherbikeapp.ui.modules.weather

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ga.lupuss.anotherbikeapp.AnotherBikeApp

import ga.lupuss.anotherbikeapp.R
import ga.lupuss.anotherbikeapp.models.weather.pojo.RawWeatherForecastData
import kotlinx.android.synthetic.main.fragment_weather.*
import javax.inject.Inject

class WeatherFragment : Fragment(), WeatherView {

    @Inject
    lateinit var presenter: WeatherPresenter

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        AnotherBikeApp.get(this.activity!!.application).weatherComponent(this).inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        presenter.requestWeatherData()

        return inflater.inflate(R.layout.fragment_weather, container, false)
    }

    @SuppressLint("SetTextI18n")
    override fun updateWeather(data: RawWeatherForecastData) {

        description.text = data
                .list
                .first()
                .weather
                .first()
                .description
                .capitalize()

        temperature.text = data
                .list
                .first()
                .main
                .temp
                .toString() + " ℃"
    }

    override fun loadWeatherImage(name: String) {
        weatherImage.setImageResource(
                requireContext().resources.getIdentifier("weather$name", "drawable", requireContext().packageName)
        )
    }
}
