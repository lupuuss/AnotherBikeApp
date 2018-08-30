package ga.lupuss.anotherbikeapp.models.android

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import ga.lupuss.anotherbikeapp.AppTheme
import ga.lupuss.anotherbikeapp.AppUnit
import ga.lupuss.anotherbikeapp.R
import ga.lupuss.anotherbikeapp.models.base.PreferencesInteractor

class AndroidPreferencesInteractor(context: Context) : PreferencesInteractor() {

    private val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    private val themeKey = context.getString(R.string.prefThemesKey)
    private val speedUnitKey = context.getString(R.string.prefTrackingUnitSpeedKey)
    private val distanceUnitKey = context.getString(R.string.prefTrackingUnitDistanceKey)
    private val isMapThemeEnableKey = context.getString(R.string.prefIsMapThemeEnableKey)
    private val weatherWindSpeedUnitKey = context.getString(R.string.prefWeatherUnitWindSpeedKey)
    private val weatherTemperatureUnitKey = context.getString(R.string.prefWeatherUnitTemperatureKey)

    private val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->

        when (key) {

            themeKey -> themeListeners.forEach { it.value.onThemeChanged(appTheme) }
            speedUnitKey -> trackingUnitListeners.forEach { it.value.onTrackingUnitChanged(trackingSpeedUnit, trackingDistanceUnit) }
            distanceUnitKey -> trackingUnitListeners.forEach { it.value.onTrackingUnitChanged(trackingSpeedUnit, trackingDistanceUnit) }
            isMapThemeEnableKey -> mapThemeListeners.forEach { it.value.onMapThemeEnable(isMapThemeEnable) }
            weatherTemperatureUnitKey -> weatherUnitListeners.forEach { it.value.onWeatherUnitChanged(weatherWindSpeedUnit, weatherTemperatureUnit)}
            weatherWindSpeedUnitKey -> weatherUnitListeners.forEach { it.value.onWeatherUnitChanged(weatherWindSpeedUnit, weatherTemperatureUnit)}
        }

    }

    init {
        sharedPreferences.registerOnSharedPreferenceChangeListener(listener)
    }

    private fun putPref(key: String, value: String) {
        sharedPreferences
                .edit()
                .putString(key, value)
                .apply()
    }

    private fun putPref(key: String, value: Boolean) {
        sharedPreferences
                .edit()
                .putBoolean(key, value)
                .apply()
    }

    private fun getPref(key: String, defaultValue: String) =
            sharedPreferences.getString(key, defaultValue)

    private fun getPref(key: String, defaultValue: Boolean) =
            sharedPreferences.getBoolean(key, defaultValue)

    override var appTheme: AppTheme
        get() = AppTheme.valueOf(getPref(themeKey, AppTheme.DARK.toString()))
        set(value) {
            putPref(themeKey, value.toString())
        }

    override var trackingSpeedUnit: AppUnit.Speed
        get() = AppUnit.Speed.valueOf(getPref(speedUnitKey, AppUnit.Speed.KM_H.toString()))
        set(value) {
            putPref(speedUnitKey, value.toString())
        }
    override var trackingDistanceUnit: AppUnit.Distance
        get() = AppUnit.Distance.valueOf(getPref(distanceUnitKey, AppUnit.Distance.KM.toString()))
        set(value) {
            putPref(distanceUnitKey, value.toString())
        }
    override var isMapThemeEnable: Boolean
        get() = getPref(isMapThemeEnableKey, true)
        set(value) {
            putPref(isMapThemeEnableKey, value)
        }

    override var weatherWindSpeedUnit: AppUnit.Speed
        get() = AppUnit.Speed.valueOf(getPref(weatherWindSpeedUnitKey, AppUnit.Speed.KM_H.toString()))
        set(value) {
            putPref(weatherWindSpeedUnitKey, value.toString())
        }

    override var weatherTemperatureUnit: AppUnit.Temperature
        get() = AppUnit.Temperature.valueOf(getPref(weatherTemperatureUnitKey, AppUnit.Temperature.CELSIUS.toString()))
        set(value) {
            putPref(weatherTemperatureUnitKey, value.toString())
        }
}