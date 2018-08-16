package ga.lupuss.anotherbikeapp.base

import android.annotation.SuppressLint
import android.os.Bundle
import ga.lupuss.anotherbikeapp.AppTheme
import ga.lupuss.anotherbikeapp.R
import ga.lupuss.anotherbikeapp.models.base.PreferencesInteractor
import javax.inject.Inject


abstract class ThemedBaseActivity : BaseActivity(), BaseView, PreferencesInteractor.OnThemeChangedListener {


    @Inject
    lateinit var preferencesInteractor: PreferencesInteractor

    @SuppressLint("ShowToast")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(preferencesInteractor.appTheme)
        preferencesInteractor.addOnThemeChangedListener(this, this)
    }

    override fun onDestroy() {
        super.onDestroy()
        preferencesInteractor.removeOnThemeChangedListener(this)
    }

    private fun setTheme(theme: AppTheme) {

        setTheme(when (theme) {
            AppTheme.LIGHT -> R.style.LightTheme
            AppTheme.DARK -> R.style.DarkTheme
        })
    }

    private fun applyTheme(theme: AppTheme) {
        setTheme(theme)
        recreate()
    }

    override fun onThemeChanged(theme: AppTheme) {

        applyTheme(theme)
    }
}