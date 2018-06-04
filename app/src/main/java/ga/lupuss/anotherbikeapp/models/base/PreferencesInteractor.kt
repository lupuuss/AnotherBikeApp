package ga.lupuss.anotherbikeapp.models.base

import ga.lupuss.anotherbikeapp.AppTheme
import ga.lupuss.anotherbikeapp.models.dataclass.Statistic
import timber.log.Timber

abstract class PreferencesInteractor {

    interface OnThemeChangedListener {
        fun onThemeChanged(theme: AppTheme)
    }

    interface OnUnitChangedListener {
        fun onUnitChanged(speedUnit: Statistic.Unit, distanceUnit: Statistic.Unit)
    }

    protected val themeListeners = mutableMapOf<Any, PreferencesInteractor.OnThemeChangedListener>()
    protected val unitListeners = mutableMapOf<Any, PreferencesInteractor.OnUnitChangedListener>()

    abstract var appTheme: AppTheme
    abstract var speedUnit: Statistic.Unit
    abstract var distanceUnit: Statistic.Unit

    fun addOnThemeChangedListener(owner: Any,
                                  onThemeChangedListener: PreferencesInteractor.OnThemeChangedListener) {

        themeListeners[owner] = onThemeChangedListener
        Timber.d("New theme listener: $owner")
    }

    fun removeOnThemeChangedListener(owner: Any) {

        Timber.d("Remove theme listener: $owner!")
        themeListeners.remove(owner) ?: Timber.w("Removing listener failed!")
    }

    fun addOnUnitChangedListener(owner: Any,
                                 onUnitChangedListener: PreferencesInteractor.OnUnitChangedListener) {

        unitListeners[owner] = onUnitChangedListener
    }

    fun removeOnUnitChangedListener(owner: Any) {

        unitListeners.remove(owner)
    }
}