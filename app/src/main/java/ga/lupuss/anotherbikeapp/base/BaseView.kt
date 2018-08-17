package ga.lupuss.anotherbikeapp.base

import android.location.Location
import ga.lupuss.anotherbikeapp.Message

interface BaseView {

    fun postMessage(message: Message)
    fun makeToast(str: String)
    fun isOnline(): Boolean
    fun finishActivity()
    fun provideLocationPermission(onLocationPermissionRequestResult: ((isSuccessful: Boolean) -> Unit))
    fun provideLocationPermission(onLocationPermissionGranted: () -> Unit, onLocationPermissionRefused: () -> Unit)
    fun requestSingleLocationUpdate(onComplete: (Boolean, Location?) -> Unit)
}