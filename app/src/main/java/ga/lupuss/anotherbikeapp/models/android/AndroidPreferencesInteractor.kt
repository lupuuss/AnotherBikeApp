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
    private val speedUnitKey = context.getString(R.string.prefUnitSpeedKey)
    private val distanceUnitKey = context.getString(R.string.prefUnitDistanceKey)
    private val isMapThemeEnableKey = context.getString(R.string.prefIsMapThemeEnableKey)

    private val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->

        when (key) {

            themeKey -> themeListeners.forEach { it.value.onThemeChanged(appTheme) }
            speedUnitKey -> unitListeners.forEach { it.value.onUnitChanged(speedUnit, distanceUnit) }
            distanceUnitKey -> unitListeners.forEach { it.value.onUnitChanged(speedUnit, distanceUnit) }
            isMapThemeEnableKey -> mapThemeListeners.forEach { it.value.onMapThemeEnable(isMapThemeEnable) }
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

    override var speedUnit: AppUnit.Speed
        get() = AppUnit.Speed.valueOf(getPref(speedUnitKey, AppUnit.Speed.KM_H.toString()))
        set(value) {
            putPref(speedUnitKey, value.toString())
        }
    override var distanceUnit: AppUnit.Distance
        get() = AppUnit.Distance.valueOf(getPref(distanceUnitKey, AppUnit.Distance.KM.toString()))
        set(value) {
            putPref(distanceUnitKey, value.toString())
        }
    override var isMapThemeEnable: Boolean
        get() = getPref(isMapThemeEnableKey, true)
        set(value) {
            putPref(isMapThemeEnableKey, value)
        }
}