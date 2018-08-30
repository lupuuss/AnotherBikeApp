package ga.lupuss.anotherbikeapp.models.base

import ga.lupuss.anotherbikeapp.AppTheme
import ga.lupuss.anotherbikeapp.AppUnit

abstract class PreferencesInteractor {

    interface OnThemeChangedListener {
        fun onThemeChanged(theme: AppTheme)
    }

    interface OnUnitChangedListener {
        fun onUnitChanged(speedUnit: AppUnit.Speed, distanceUnit: AppUnit.Distance)
    }

    interface OnMapThemeEnableListener {
        fun onMapThemeEnable(isMapThemeEnable: Boolean)
    }

    protected val themeListeners = mutableMapOf<Any, PreferencesInteractor.OnThemeChangedListener>()
    protected val unitListeners = mutableMapOf<Any, PreferencesInteractor.OnUnitChangedListener>()
    protected val mapThemeListeners = mutableMapOf<Any, PreferencesInteractor.OnMapThemeEnableListener>()

    abstract var appTheme: AppTheme
    abstract var speedUnit: AppUnit.Speed
    abstract var distanceUnit: AppUnit.Distance
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