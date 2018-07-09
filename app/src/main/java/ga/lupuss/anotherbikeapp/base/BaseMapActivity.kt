package ga.lupuss.anotherbikeapp.base

import android.util.TypedValue
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.MapStyleOptions
import ga.lupuss.anotherbikeapp.R
import ga.lupuss.anotherbikeapp.models.base.PreferencesInteractor

abstract class BaseMapActivity : BaseActivity(), OnMapReadyCallback, PreferencesInteractor.OnMapThemeEnableListener {

    protected lateinit var map: GoogleMap

    override fun onMapReady(googleMap: GoogleMap?) {
        this.map = googleMap!!

        map.uiSettings.isZoomControlsEnabled = true

        if (preferencesInteractor.isMapThemeEnable) {

            map.setMapStyle(MapStyleOptions(getGoogleMapStyleFromTheme()))

        }

        preferencesInteractor.addOnMapThemeEnableListener(this, this)
    }

    override fun onMapThemeEnable(isMapThemeEnable: Boolean) {

        map.setMapStyle(MapStyleOptions(if (isMapThemeEnable) getGoogleMapStyleFromTheme() else ""))
    }

    private fun getGoogleMapStyleFromTheme(): String {

        val typedValue = TypedValue()
        theme.resolveAttribute(R.attr.googleMapStyleJson, typedValue, true)

        return typedValue.string as String
    }

    override fun onDestroy() {
        super.onDestroy()
        preferencesInteractor.removeOnMapThemeEnableListener(this)
    }
}