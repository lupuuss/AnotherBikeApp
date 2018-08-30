package ga.lupuss.anotherbikeapp.models.base

import ga.lupuss.anotherbikeapp.AppTheme
import ga.lupuss.anotherbikeapp.AppUnit

abstract class PreferencesInteractor {

    interface OnThemeChangedListener {
        fun onThemeChanged(theme: AppTheme)
    }

    interface OnTrackingUnitChangedListener {
        fun onTrackingUnitChanged(speedUnit: AppUnit.Speed, distanceUnit: AppUnit.Distance)
    }

    interface OnWeatherUnitChangedListener {

        fun onWeatherUnitChanged(windSpeedUnit: AppUnit.Speed, temperatureUnit: AppUnit.Temperature)
    }

    interface OnMapThemeEnableListener {
        fun onMapThemeEnable(isMapThemeEnable: Boolean)
    }

    protected val themeListeners = mutableMapOf<Any, PreferencesInteractor.OnThemeChangedListener>()
    protected val trackingUnitListeners = mutableMapOf<Any, PreferencesInteractor.OnTrackingUnitChangedListener>()
    protected val mapThemeListeners = mutableMapOf<Any, PreferencesInteractor.OnMapThemeEnableListener>()
    protected val weatherUnitListeners = mutableMapOf<Any, PreferencesInteractor.OnWeatherUnitChangedListener>()

    abstract var appTheme: AppTheme
    abstract var trackingSpeedUnit: AppUnit.Speed
    abstract var trackingDistanceUnit: AppUnit.Distance
    abstract var weatherWindSpeedUnit: AppUnit.Speed
    abstract var weatherTemperatureUnit: AppUnit.Temperature
    abstract var isMapThemeEnable: Boolean

    fun addOnThemeChangedListener(owner: Any,
                                  onThemeChangedListener: PreferencesInteractor.OnThemeChangedListener) {

        themeListeners[owner] = onThemeChangedListener
    }

    fun removeOnThemeChangedListener(owner: Any) {

        themeListeners.remove(owner)
    }

    fun addOnTrackingUnitChangedListener(owner: Any,
                                         onUnitChangedListener: PreferencesInteractor.OnTrackingUnitChangedListener) {

        trackingUnitListeners[owner] = onUnitChangedListener
    }

    fun removeOnTrackingUnitChangedListener(owner: Any) {

        trackingUnitListeners.remove(owner)
    }

    fun addOnMapThemeEnableListener(owner: Any, onMapThemeEnableListener: OnMapThemeEnableListener) {

        mapThemeListeners[owner] = onMapThemeEnableListener
    }

    fun removeOnMapThemeEnableListener(owner: Any) {

        mapThemeListeners.remove(owner)
    }

    fun addOnWeatherUnitChangedListener(owner: Any, onWeatherUnitChangedListener: OnWeatherUnitChangedListener) {

        weatherUnitListeners[owner] = onWeatherUnitChangedListener
    }

    fun removeOnWeatherUnitChangedListener(owner: Any) {

        weatherUnitListeners.remove(owner)
    }
}