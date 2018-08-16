package ga.lupuss.anotherbikeapp.base

import android.annotation.SuppressLint
import android.location.Location
import com.google.android.gms.maps.model.LatLng
import ga.lupuss.anotherbikeapp.Message

interface BaseView {

    fun postMessage(message: Message)
    fun makeToast(str: String)
    fun isOnline(): Boolean
    fun checkLocationPermission(): Boolean
    fun finishActivity()
    fun requestLocationPermission(onLocationPermissionRequestResult: (Boolean) -> Unit)
    fun requestSingleLocationUpdate(onComplete: (Boolean, Location) -> Unit)
}