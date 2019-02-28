package ga.lupuss.anotherbikeapp.ui.modules.settings

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import ga.lupuss.anotherbikeapp.R

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }
}