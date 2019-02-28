package ga.lupuss.anotherbikeapp.ui.modules.weather

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import ga.lupuss.anotherbikeapp.*

import ga.lupuss.anotherbikeapp.base.LabeledFragment
import ga.lupuss.anotherbikeapp.models.base.PreferencesInteractor
import ga.lupuss.anotherbikeapp.models.dataclass.WeatherData
import ga.lupuss.anotherbikeapp.ui.extensions.isGone
import ga.lupuss.anotherbikeapp.ui.extensions.isVisible
import kotlinx.android.synthetic.main.fragment_weather.*
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import kotlin.math.roundToInt

class WeatherFragment
    : LabeledFragment(),
        WeatherView,
        View.OnClickListener,
        SeekBar.OnSeekBarChangeListener {

    @Inject
    lateinit var presenter: WeatherPresenter

    @Inject
    lateinit var preferencesInteractor: PreferencesInteractor

    private lateinit var windSpeedUnit: AppUnit.Speed
    private lateinit var temperatureUnit: AppUnit.Temperature

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

        windSpeedUnit = preferencesInteractor.weatherWindSpeedUnit
        temperatureUnit = preferencesInteractor.weatherTemperatureUnit

        super.onAttachPostVerification(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val viewGroup = super.onCreateView(inflater, container, savedInstanceState) as ViewGroup
        attachChildLayoutToParent(
                inflater.inflate(R.layout.fragment_weather, viewGroup, false),
                viewGroup as ConstraintLayout
        )

        return viewGroup
    }

    override fun onViewCreatedPostVerification(view: View, savedInstanceState: Bundle?) {
        super.onViewCreatedPostVerification(view, savedInstanceState)

        setLabel(R.string.weather)
        locationInfo.setOnClickListener(this)
        weatherSeekBar.setOnSeekBarChangeListener(this)
        weatherExpandButton.setOnClickListener(this)
        presenter.notifyOnViewReady()
    }

    override fun refreshUnits(windSpeedUnit: AppUnit.Speed, temperatureUnit: AppUnit.Temperature) {

        this.windSpeedUnit = windSpeedUnit
        this.temperatureUnit = temperatureUnit
    }

    override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
        presenter.onWeatherSeekBarPositionChanged(p1)
    }

    override fun onStartTrackingTouch(p0: SeekBar?) {}

    override fun onStopTrackingTouch(p0: SeekBar?) {}

    override fun onClick(p0: View?) {

        when(p0!!.id) {

            R.id.locationInfo -> {

                presenter.onClickLocationInfo()
            }

            R.id.weatherDayLayout -> {

                presenter.onClickWeatherDay(p0.tag as Int)
            }

            R.id.weatherExpandButton -> {

                val isHidden = weatherExpandButton.rotation == 0F

                weatherExpandButton.rotation =  if (isHidden) {
                    180F
                } else {
                    0F
                }

                horizontalScrollView.isGone = !isHidden
            }
        }
    }

    override fun onClickRefreshButton() {

        presenter.onClickRefreshButton()
    }

    @SuppressLint("SetTextI18n")
    override fun setWeather(data: WeatherData) {

        val realPosition = 0

        val locale =  AnotherBikeApp
                .get(this.requireActivity().application)
                .anotherBikeAppComponent
                .providesLocale()

        weatherHelper(data, realPosition, locale)

        hoursContainer.removeAllViews()
        for (x in 0..7) {
            val view = layoutInflater.inflate(
                    R.layout.fragment_weather_seekbar_hour_text, hoursContainer, false
            ) as TextView

            view.text = timeToHourMinutes(locale, data.forecast[x].time)
            hoursContainer.addView(view)
        }

        daysContainer.removeAllViews()

        setVisibleWeatherDetails(true)

        data.daysInfo.forEachIndexed { index, it ->

            val dayView = this.layoutInflater.inflate(R.layout.fragment_weather_day, daysContainer, false)

            dayView.findViewById<ImageView>(R.id.weatherDayImage).setImageResource(
                    resolveIcon(it.icon)
            )

            val min = temperatureUnit.convertFunction.invoke(it.minTemp)
            val max = temperatureUnit.convertFunction.invoke(it.maxTemp)

            dayView.findViewById<TextView>(R.id.minTemp).text = "${min.roundToInt()} ${resourceResolver.resolve(temperatureUnit)}"
            dayView.findViewById<TextView>(R.id.maxTemp).text = "${max.roundToInt()} ${resourceResolver.resolve(temperatureUnit)}"
            dayView.findViewById<TextView>(R.id.dayOfWeekText).text = SimpleDateFormat("E" ,locale)
                    .format(data.forecast[it.startIndex].time.time)
            dayView.tag = index
            dayView.setOnClickListener(this)

            if (index == 0) {

                dayView.background = ContextCompat.getDrawable(this.requireContext(), R.drawable.weather_day_back_current)
            }

            daysContainer.addView(dayView)
        }
    }

    private fun setVisibleWeatherDetails(isVisible: Boolean) {

        snowfallImage.isVisible = isVisible
        snowfallValue.isVisible = isVisible
        rainfallImage.isVisible = isVisible
        rainfallValue.isVisible = isVisible
        humidityImage.isVisible = isVisible
        humidityValue.isVisible = isVisible

        cloudsImage.isVisible = isVisible
        cloudsValue.isVisible = isVisible
        windImage.isVisible = isVisible
        windValue.isVisible = isVisible
        pressureImage.isVisible = isVisible
        pressureValue.isVisible = isVisible
    }

    override fun updateWeather(data: WeatherData, position: Int, currentDay: Int, dayBefore: Int) {
        val realPosition = position + data.daysInfo[currentDay].startIndex

        val locale =  AnotherBikeApp
                .get(this.requireActivity().application)
                .anotherBikeAppComponent
                .providesLocale()

        weatherHelper(data, realPosition, locale)

        if (dayBefore != currentDay) {

            for (child in 0..7) {

                (hoursContainer.getChildAt(child) as TextView).text =
                        timeToHourMinutes(locale, data.forecast[data.daysInfo[currentDay].startIndex + child].time)
            }

            daysContainer.getChildAt(currentDay).setBackgroundResource(R.drawable.weather_day_back_current)
            daysContainer.getChildAt(dayBefore).setBackgroundResource(R.drawable.weather_day_back)
        }
    }

    @SuppressLint("SetTextI18n")
    private fun weatherHelper(data: WeatherData, realPosition: Int, locale: Locale) {

        val weatherUnit =  data.forecast[realPosition]

        hour.text = if (realPosition == 0) getString(R.string.now)

        else timeToHourMinutes(
                locale,
                weatherUnit.time
        )

        dayText.text = SimpleDateFormat("EEEE" ,locale)
                .format(weatherUnit.time.time)
                .capitalize()

        val temp = Math.round(temperatureUnit.convertFunction.invoke(weatherUnit.temperature))

        description.text = weatherUnit.description.capitalize()
        temperature.text = "$temp ${resourceResolver.resolve(temperatureUnit)}"
        locationText.text = data.location ?: requireContext().getString(R.string.nameNotAvailable)
        coordsText.text = "${data.lat}, ${data.lng}"

        weatherImage.setImageResource(
                resolveIcon(weatherUnit.icon)
        )

        snowfallValue.text = "${weatherUnit.snowVolume.round(2)} mm"
        rainfallValue.text = "${weatherUnit.rainVolume.round(2)} mm"
        humidityValue.text = "${weatherUnit.humidity}%"

        val windSpeed = windSpeedUnit.convertFunction.invoke(weatherUnit.windSpeed).round(1)

        cloudsValue.text = "${weatherUnit.clouds}%"
        windValue.text = "$windSpeed ${resourceResolver.resolve(windSpeedUnit)}"
        pressureValue.text = "${weatherUnit.pressure.roundToInt()} hPa"
    }

    private fun resolveIcon(weatherIcon: WeatherIcon): Int {
        return when(weatherIcon) {

            WeatherIcon.SUNNY_D -> R.drawable.weather_clear_sky_day
            WeatherIcon.SUNNY_N -> R.drawable.weather_clear_sky_night
            WeatherIcon.FEW_CLOUDS_D -> R.drawable.weather_few_clouds_day
            WeatherIcon.FEW_CLOUDS_N -> R.drawable.weather_few_clodus_night
            WeatherIcon.SCATTERED_CLOUDS_D -> R.drawable.weather_scattered_clouds
            WeatherIcon.SCATTERED_CLOUDS_N -> R.drawable.weather_scattered_clouds
            WeatherIcon.BROKEN_CLOUDS_D -> R.drawable.weather_scattered_clouds
            WeatherIcon.BROKEN_CLOUDS_N -> R.drawable.weather_scattered_clouds
            WeatherIcon.SHOWER_RAIN_D -> R.drawable.weather_shower_rain
            WeatherIcon.SHOWER_RAIN_N -> R.drawable.weather_shower_rain
            WeatherIcon.RAIN_D -> R.drawable.weather_rain_day
            WeatherIcon.RAIN_N -> R.drawable.weather_rain_night
            WeatherIcon.THUNDERSTORM_D -> R.drawable.weather_thunderstorm
            WeatherIcon.THUNDERSTORM_N -> R.drawable.weather_thunderstorm
            WeatherIcon.SNOW_D -> R.drawable.weather_snow_day
            WeatherIcon.SNOW_N -> R.drawable.weather_snow_night
            WeatherIcon.MIST_D -> R.drawable.weather_mist_day
            WeatherIcon.MIST_N -> R.drawable.weather_mist_night
            WeatherIcon.EMPTY -> R.drawable.ic_priority_high_64dp
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
