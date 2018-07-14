package ga.lupuss.anotherbikeapp.models.base

import ga.lupuss.anotherbikeapp.AppTheme
import ga.lupuss.anotherbikeapp.models.dataclass.Statistic

abstract class PreferencesInteractor {

    interface OnThemeChangedListener {
        fun onThemeChanged(theme: AppTheme)
    }

    interface OnUnitChangedListener {
        fun onUnitChanged(speedUnit: Statistic.Unit.Speed, distanceUnit: Statistic.Unit.Distance)
    }

    interface OnMapThemeEnableListener {
        fun onMapThemeEnable(isMapThemeEnable: Boolean)
    }

    protected val themeListeners = mutableMapOf<Any, PreferencesInteractor.OnThemeChangedListener>()
    protected val unitListeners = mutableMapOf<Any, PreferencesInteractor.OnUnitChangedListener>()
    protected val mapThemeListeners = mutableMapOf<Any, PreferencesInteractor.OnMapThemeEnableListener>()

    abstract var appTheme: AppTheme
    abstract var speedUnit: Statistic.Unit.Speed
    abstract var distanceUnit: Statistic.Unit.Distance
    abstract var isMapThemeEnable: Boolean

    fun addOnThemeChangedListener(owner: Any,
                                  onThemeChangedListener: PreferencesInteractor.OnThemeChangedListener) {

        themeListeners[owner] = onThemeChangedListener
    }

    fun removeOnThemeChangedListener(owner: Any) {

        themeListeners.remove(owner)
    }

    fun addOnUnitChangedListener(owner: Any,
                                 onUnitChangedListener: PreferencesInteractor.OnUnitChangedListener) {

        unitListeners[owner] = onUnitChangedListener
    }

    fun removeOnUnitChangedListener(owner: Any) {

        unitListeners.remove(owner)
    }

    fun addOnMapThemeEnableListener(owner: Any, onMapThemeEnableListener: OnMapThemeEnableListener) {

        mapThemeListeners[owner] = onMapThemeEnableListener
    }

    fun removeOnMapThemeEnableListener(owner: Any) {

        mapThemeListeners.remove(owner)
    }
}