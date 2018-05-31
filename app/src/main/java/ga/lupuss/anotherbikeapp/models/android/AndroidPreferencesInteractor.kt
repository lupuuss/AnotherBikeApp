package ga.lupuss.anotherbikeapp.models.android

import android.content.SharedPreferences
import ga.lupuss.anotherbikeapp.AppTheme
import ga.lupuss.anotherbikeapp.models.interfaces.PreferencesInteractor
import ga.lupuss.anotherbikeapp.models.trackingservice.statisticsmanager.statistics.Statistic

class AndroidPreferencesInteractor(private val sharedPreferences: SharedPreferences) : PreferencesInteractor() {

    private val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->

        when (key) {

            THEME_KEY -> themeListeners.forEach { it.value.onThemeChanged(appTheme) }
            SPEED_UNIT_KEY -> unitListeners.forEach { it.value.onUnitChanged(speedUnit, distanceUnit) }
            DISTANCE_UNIT_KEY -> unitListeners.forEach { it.value.onUnitChanged(speedUnit, distanceUnit) }
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
        get() = AppTheme.valueOf(getPref(THEME_KEY, AppTheme.GREY.toString()))
        set(value) {
            putPref(THEME_KEY, value.toString())
        }

    override var speedUnit: Statistic.Unit
        get() = Statistic.Unit.valueOf(getPref(SPEED_UNIT_KEY, Statistic.Unit.KM_H.toString()))
        set(value) {
            putPref(SPEED_UNIT_KEY, value.toString())
        }
    override var distanceUnit: Statistic.Unit
        get() = Statistic.Unit.valueOf(getPref(DISTANCE_UNIT_KEY, Statistic.Unit.KM.toString()))
        set(value) {

            putPref(DISTANCE_UNIT_KEY, value.toString())
        }

    companion object {

        const val THEME_KEY = "themeKey"
        const val SPEED_UNIT_KEY = "speedUnitKey"
        const val DISTANCE_UNIT_KEY = "distanceUnitKey"
    }
}