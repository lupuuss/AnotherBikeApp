package ga.lupuss.anotherbikeapp.models.android

import android.content.Context
import android.content.SharedPreferences
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.nhaarman.mockito_kotlin.times
import com.nhaarman.mockito_kotlin.verify
import ga.lupuss.anotherbikeapp.R
import ga.lupuss.anotherbikeapp.models.base.PreferencesInteractor
import org.junit.Test

class AndroidPreferencesInteractorTest {

    private val sharedListeners = mutableListOf<SharedPreferences.OnSharedPreferenceChangeListener>()

    private val themeKey = "themesKey"
    private val unitSpeedKey = "unitSpeedKey"
    private val distanceSpeedKey = "distanceSpeedKey"
    private val isMapEnableKey = "isMapThemeEnableKey"
    private val weatherWindSpeedUnitKey = "weatherUnitWindSpeedKey"
    private val weatherTemperatureUnitKey = "weatherUnitTemperatureKey"

    private val sharedPrefs: SharedPreferences = mock {
        on { registerOnSharedPreferenceChangeListener(any()) }
                .then { sharedListeners.add(it.getArgument(0) as SharedPreferences.OnSharedPreferenceChangeListener) }
        on { getString(any(), any()) }.then { it.getArgument(1) as String }
    }


    private val context: Context = mock {
        on { getString(R.string.prefThemesKey) }.then { themeKey }
        on { getString(R.string.prefTrackingUnitSpeedKey) }.then { unitSpeedKey }
        on { getString(R.string.prefTrackingUnitDistanceKey) }.then { distanceSpeedKey }
        on { getString(R.string.prefIsMapThemeEnableKey) }.then { isMapEnableKey }
        on { getString(R.string.prefWeatherUnitTemperatureKey) }.then { weatherTemperatureUnitKey }
        on { getString(R.string.prefWeatherUnitWindSpeedKey) }.then { weatherWindSpeedUnitKey }
    }
    private val preferencesInteractor = AndroidPreferencesInteractor(context, sharedPrefs)

    @Test
    fun androidPreferencesInteractor_shouldNotifyAboutThemeChanges() {

        val onThemeListener: PreferencesInteractor.OnThemeChangedListener = mock {}

        preferencesInteractor.addOnThemeChangedListener(this, onThemeListener)
        sharedListeners.forEach {
            it.onSharedPreferenceChanged(mock {}, context.getString(R.string.prefThemesKey))
        }

        verify(onThemeListener, times(1)).onThemeChanged(any())
    }

    @Test
    fun androidPreferencesInteractor_shouldNotifyAbouttTrackingUnitsChanges() {

        val onTrackingUnit: PreferencesInteractor.OnTrackingUnitChangedListener = mock {}

        preferencesInteractor.addOnTrackingUnitChangedListener(this, onTrackingUnit)
        sharedListeners.forEach {
            it.onSharedPreferenceChanged(mock {}, context.getString(R.string.prefTrackingUnitDistanceKey))
            it.onSharedPreferenceChanged(mock {}, context.getString(R.string.prefTrackingUnitSpeedKey))
        }

        verify(onTrackingUnit, times(2)).onTrackingUnitChanged(any(), any())
    }

    @Test
    fun androidPreferencesInteractor_shouldNotifyAboutMapThemeChanges() {

        val onMap: PreferencesInteractor.OnMapThemeEnableListener = mock {}

        preferencesInteractor.addOnMapThemeEnableListener(this, onMap)
        sharedListeners.forEach {
            it.onSharedPreferenceChanged(mock {}, context.getString(R.string.prefIsMapThemeEnableKey))
        }

        verify(onMap, times(1)).onMapThemeEnable(any())
    }

    @Test
    fun androidPreferencesInteractor_shouldNotifyAboutWeatherUnitsChanges() {

        val onWeather: PreferencesInteractor.OnWeatherUnitChangedListener = mock {}

        preferencesInteractor.addOnWeatherUnitChangedListener(this, onWeather)
        sharedListeners.forEach {
            it.onSharedPreferenceChanged(mock {}, context.getString(R.string.prefWeatherUnitTemperatureKey))
            it.onSharedPreferenceChanged(mock {}, context.getString(R.string.prefWeatherUnitWindSpeedKey))
        }

        verify(onWeather, times(2)).onWeatherUnitChanged(any(), any())
    }

}