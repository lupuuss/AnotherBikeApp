package ga.lupuss.anotherbikeapp.ui.modules.weather

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ga.lupuss.anotherbikeapp.AnotherBikeApp

import ga.lupuss.anotherbikeapp.R
import ga.lupuss.anotherbikeapp.di.BaseFragment
import ga.lupuss.anotherbikeapp.models.dataclass.WeatherData
import kotlinx.android.synthetic.main.fragment_weather.*
import javax.inject.Inject

class WeatherFragment : BaseFragment(), WeatherView, View.OnClickListener {

    @Inject
    lateinit var presenter: WeatherPresenter

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        AnotherBikeApp.get(this.activity!!.application).weatherComponent(this).inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_weather, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        refreshButton.setOnClickListener(this)
        locationInfo.setOnClickListener(this)
        presenter.notifyOnViewReady()
    }

    override fun onClick(p0: View?) {

        when(p0!!.id) {
            R.id.refreshButton -> {

                presenter.onClickRefreshButton()
            }

            R.id.locationInfo -> {

                presenter.onClickLocationInfo()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun updateWeather(data: WeatherData) {

        description.text = data.forecast.first().description.capitalize()
        temperature.text = data.forecast.first().temperature.toString() + " ℃"

        locationText.text = data.location ?: requireContext().getString(R.string.nameNotAvailable)

        coordsText.text = "${data.lat}, ${data.lng}"

        weatherImage.setImageResource(
                requireContext().resources.getIdentifier(
                        "weather${data.forecast.first().iconName}",
                        "drawable", requireContext().packageName
                )
        )
    }

    override fun redirectToGoogleMaps(lat: Double, lng: Double, name: String?) {

        val nameVal = name ?: getString(R.string.unknownLocation)

        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=$lat,$lng($nameVal)"))
        intent.resolveActivity(context!!.packageManager)?.let {
            startActivity(intent)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        presenter.notifyOnDestroy(true)
    }
}
