package ga.lupuss.anotherbikeapp.models.android

import android.content.Context
import android.content.SharedPreferences
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import ga.lupuss.anotherbikeapp.AppTheme
import ga.lupuss.anotherbikeapp.R
import ga.lupuss.anotherbikeapp.models.base.PreferencesInteractor
import ga.lupuss.anotherbikeapp.models.dataclass.Statistic
import org.junit.Test

class AndroidPreferencesInteractorTest {

    private val sharedListeners = mutableListOf<SharedPreferences.OnSharedPreferenceChangeListener>()

    private val themeKey = "themesKey"
    private val unitSpeedKey = "unitSpeedKey"
    private val distanceSpeedKey = "distanceSpeedKey"

    private val sharedPrefs: SharedPreferences = mock {
        on { registerOnSharedPreferenceChangeListener(any()) }
                .then { sharedListeners.add(it.getArgument(0) as SharedPreferences.OnSharedPreferenceChangeListener) }
        on { getString(any(), any()) }.then { it.getArgument(1) as String }
    }


    private val context: Context = mock {
        on { getString(R.string.prefThemesKey) }.then { themeKey }
        on { getString(R.string.prefUnitSpeedKey) }.then { unitSpeedKey }
        on { getString(R.string.prefUnitDistanceKey) }.then { distanceSpeedKey }
    }
    private val preferencesInteractor = AndroidPreferencesInteractor(sharedPrefs, context)

    @Test
    fun androidPreferencesInteractor_shouldNotifyAboutSharedPreferencesChanges() {

        var triggeredThemeChanges = 0
        var triggeredUnitChanges = 0

        preferencesInteractor.addOnThemeChangedListener(
                this,
                object : PreferencesInteractor.OnThemeChangedListener {
                    override fun onThemeChanged(theme: AppTheme) {
                        triggeredThemeChanges++
                    }

                }
        )
        preferencesInteractor.addOnUnitChangedListener(
                this,
                object : PreferencesInteractor.OnUnitChangedListener {
                    override fun onUnitChanged(speedUnit: Statistic.Unit, distanceUnit: Statistic.Unit) {
                        triggeredUnitChanges++
                    }

                }
        )

        // triggers sharedPreferences listeners
        sharedListeners.forEach {
            it.onSharedPreferenceChanged(any(), context.getString(R.string.prefThemesKey))
            it.onSharedPreferenceChanged(any(), context.getString(R.string.prefUnitDistanceKey))
            it.onSharedPreferenceChanged(any(), context.getString(R.string.prefUnitSpeedKey))
        }

        assert(triggeredThemeChanges == 1)
        assert(triggeredUnitChanges == 2)

    }

}