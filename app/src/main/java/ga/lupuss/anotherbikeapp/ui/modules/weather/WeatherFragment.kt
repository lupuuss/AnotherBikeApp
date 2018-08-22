package ga.lupuss.anotherbikeapp.ui.modules.weather

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import ga.lupuss.anotherbikeapp.AnotherBikeApp

import ga.lupuss.anotherbikeapp.R
import ga.lupuss.anotherbikeapp.Text
import ga.lupuss.anotherbikeapp.base.BaseFragment
import ga.lupuss.anotherbikeapp.models.dataclass.WeatherData
import ga.lupuss.anotherbikeapp.timeToHourMinutes
import ga.lupuss.anotherbikeapp.ui.extensions.isVisible
import kotlinx.android.synthetic.main.fragment_weather.*
import java.text.SimpleDateFormat
import javax.inject.Inject
import kotlin.math.roundToInt

class WeatherFragment : BaseFragment(), WeatherView, View.OnClickListener, SeekBar.OnSeekBarChangeListener {

    override var isRefreshButtonVisible: Boolean = true
        set(value) {

            refreshButton?.isVisible = value
        }

    override var isRefreshProgressBarVisible: Boolean = false
        set(value) {

            refreshProgressBar?.isVisible = value
        }
    @Inject
    lateinit var presenter: WeatherPresenter

    override fun onAttach(context: Context?) {
        requiresVerification()
        super.onAttach(context)
    }

    override fun onAttachPostVerification(context: Context?) {

        // Dagger MUST be first
        // super method requires it

        AnotherBikeApp
                .get(this.activity!!.application)
                .weatherComponent(this)
                .inject(this)

        super.onAttachPostVerification(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_weather, container, false)
    }

    override fun onViewCreatedPostVerification(view: View, savedInstanceState: Bundle?) {
        super.onViewCreatedPostVerification(view, savedInstanceState)

        refreshButton.setOnClickListener(this)
        locationInfo.setOnClickListener(this)
        weatherSeekBar.setOnSeekBarChangeListener(this)
        presenter.notifyOnViewReady()
    }

    override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
        presenter.onWeatherSeekBarPositionChanged(p1)
    }

    override fun onStartTrackingTouch(p0: SeekBar?) {}

    override fun onStopTrackingTouch(p0: SeekBar?) {}

    override fun onClick(p0: View?) {

        when(p0!!.id) {

            R.id.refreshButton -> {

                presenter.onClickRefreshButton()
            }

            R.id.locationInfo -> {

                presenter.onClickLocationInfo()
            }

            R.id.weatherDayLayout -> {

                presenter.onClickWeatherDay(p0.tag as Int)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun setWeather(data: WeatherData, position: Int, day: Int) {

        val realPosition = position + data.daysInfo[day].startIndex

        val locale =  AnotherBikeApp
                .get(this.requireActivity().application)
                .anotherBikeAppComponent
                .providesLocale()

        hour.text = if (position == 0 && day == 0) getString(R.string.now)

        else timeToHourMinutes(
                locale,
                data.forecast[realPosition].time
        )

        dayText.text = SimpleDateFormat("EEEE" ,locale)
                .format(data.forecast[realPosition].time.time)
                .capitalize()

        description.text = data.forecast[realPosition].description.capitalize()
        temperature.text = Math.round(data.forecast[realPosition].temperature).toString() + " ℃"

        locationText.text = data.location ?: requireContext().getString(R.string.nameNotAvailable)

        coordsText.text = "${data.lat}, ${data.lng}"

        weatherImage.setImageResource(
                requireContext().resources.getIdentifier(
                        "weather${data.forecast[realPosition].iconName}",
                        "drawable", requireContext().packageName
                )
        )

        daysContainer.removeAllViews()

        data.daysInfo.forEachIndexed { index, it ->

            val dayView = this.layoutInflater.inflate(R.layout.fragment_weather_day, daysContainer, false)

            dayView.findViewById<ImageView>(R.id.weatherDayImage).setImageResource(
                    requireContext().resources.getIdentifier(
                            "weather${it.icon}",
                            "drawable", requireContext().packageName
                    )
            )

            dayView.findViewById<TextView>(R.id.minTemp).text = it.minTemp.roundToInt().toString() + " ℃"
            dayView.findViewById<TextView>(R.id.maxTemp).text = it.maxTemp.roundToInt().toString() + " ℃"
            dayView.findViewById<TextView>(R.id.dayOfWeekText).text = SimpleDateFormat("E" ,locale)
                    .format(data.forecast[it.startIndex].time.time)
            dayView.tag = index
            dayView.setOnClickListener(this)

            if (index == day) {

                dayView.background = ContextCompat.getDrawable(this.requireContext(), R.drawable.weather_day_back_current)
            }

            daysContainer.addView(dayView)
        }
    }

    override fun redirectToGoogleMaps(lat: Double, lng: Double, name: String?) {

        val nameVal = name ?: getString(R.string.unknownLocation)

        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("geo:0,0?q=$lat,$lng($nameVal)"))
        intent.resolveActivity(context!!.packageManager)?.let {
            startActivity(intent)
        }
    }

    override fun resetSeekBar() {

        weatherSeekBar.progress = 0
    }

    override fun onDestroyViewPostVerification() {
        super.onDestroyViewPostVerification()
        presenter.notifyOnDestroy(true)
    }
}
