package ga.lupuss.anotherbikeapp.ui.modules.weather

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import ga.lupuss.anotherbikeapp.AnotherBikeApp
import ga.lupuss.anotherbikeapp.Message

import ga.lupuss.anotherbikeapp.R
import ga.lupuss.anotherbikeapp.base.BaseActivity
import ga.lupuss.anotherbikeapp.models.dataclass.WeatherData
import kotlinx.android.synthetic.main.fragment_weather.*
import javax.inject.Inject

class WeatherFragment : Fragment(), WeatherView, View.OnClickListener {

    @Inject
    lateinit var presenter: WeatherPresenter

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        AnotherBikeApp.get(this.activity!!.application).weatherComponent(this).inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_weather, container, false)

        presenter.notifyOnViewReady()
        view.findViewById<ImageButton>(R.id.refreshButton).setOnClickListener(this)

        return view
    }

    override fun onClick(p0: View?) {

        when(p0!!.id) {
            R.id.refreshButton -> {

                presenter.onRefreshButtonClick()
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun updateWeather(data: WeatherData) {

        description.text = data.forecast.first().description
        temperature.text = data.forecast.first().temperature.toString() + " ℃"

        locationText.text = if (data.location != null)
            data.location
        else
            requireContext().getString(R.string.nameNotAvailable)

        coordsText.text = "${data.lat}, ${data.lng}"

        weatherImage.setImageResource(
                requireContext().resources.getIdentifier(
                        "weather${data.forecast.first().iconName}",
                        "drawable", requireContext().packageName
                )
        )
    }

    override fun postMessage(message: Message) {
        (activity as? BaseActivity)?.postMessage(message)
    }

    override fun makeToast(str: String) {
        (activity as? BaseActivity)?.makeToast(str)
    }

    override fun isOnline(): Boolean {
        return (activity as? BaseActivity)?.isOnline() ?: false
    }

    override fun checkLocationPermission(): Boolean {
        return  (activity as? BaseActivity)?.checkLocationPermission() ?: false
    }

    override fun finishActivity() {
        (activity as? BaseActivity)?.finishActivity()
    }

    override fun requestLocationPermission(onLocationPermissionRequestResult: (Boolean) -> Unit) {
        (activity as? BaseActivity)?.requestLocationPermission(onLocationPermissionRequestResult)
    }
}
