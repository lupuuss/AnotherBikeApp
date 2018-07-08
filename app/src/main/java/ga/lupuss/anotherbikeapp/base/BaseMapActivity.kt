package ga.lupuss.anotherbikeapp.base

import android.util.TypedValue
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.MapStyleOptions
import ga.lupuss.anotherbikeapp.R

abstract class BaseMapActivity : BaseActivity(), OnMapReadyCallback {

    protected lateinit var map: GoogleMap

    override fun onMapReady(googleMap: GoogleMap?) {
        this.map = googleMap!!
        getGoogleMapStyleFromTheme()?.let {

            map.setMapStyle(MapStyleOptions(it))
        }
    }

    private fun getGoogleMapStyleFromTheme(): String? {

        val typedValue = TypedValue()
        theme.resolveAttribute(R.attr.googleMapStyleJson, typedValue, true)

        return typedValue.string as String
    }
}