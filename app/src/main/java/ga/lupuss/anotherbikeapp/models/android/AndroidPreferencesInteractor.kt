package ga.lupuss.anotherbikeapp.models.android

import android.content.Context
import android.content.SharedPreferences
import ga.lupuss.anotherbikeapp.AppTheme
import ga.lupuss.anotherbikeapp.R
import ga.lupuss.anotherbikeapp.models.base.PreferencesInteractor
import ga.lupuss.anotherbikeapp.models.dataclass.Statistic

class AndroidPreferencesInteractor(private val sharedPreferences: SharedPreferences,
                                   context: Context) : PreferencesInteractor() {

    private val themeKey = context.getString(R.string.prefThemesKey)
    private val speedUnitKey = context.getString(R.string.prefUnitSpeedKey)
    private val distanceUnitKey = context.getString(R.string.prefUnitDistanceKey)

    private val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->

        when (key) {

            themeKey -> themeListeners.forEach { it.value.onThemeChanged(appTheme) }
            speedUnitKey -> unitListeners.forEach { it.value.onUnitChanged(speedUnit, distanceUnit) }
            distanceUnitKey -> unitListeners.forEach { it.value.onUnitChanged(speedUnit, distanceUnit) }
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

    private fun getPref(key: String, defaultValue: String) =
            sharedPreferences.getString(key, defaultValue)

    override var appTheme: AppTheme
        get() = AppTheme.valueOf(getPref(themeKey, AppTheme.GREY.toString()))
        set(value) {
            putPref(themeKey, value.toString())
        }

    override var speedUnit: Statistic.Unit
        get() = Statistic.Unit.valueOf(getPref(speedUnitKey, Statistic.Unit.KM_H.toString()))
        set(value) {
            putPref(speedUnitKey, value.toString())
        }
    override var distanceUnit: Statistic.Unit
        get() = Statistic.Unit.valueOf(getPref(distanceUnitKey, Statistic.Unit.KM.toString()))
        set(value) {

            putPref(distanceUnitKey, value.toString())
        }
}