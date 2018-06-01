package ga.lupuss.anotherbikeapp.base

import ga.lupuss.anotherbikeapp.Message

interface BaseView {

    fun postMessage(message: Message)
    fun makeToast(str: String)
    fun isOnline(): Boolean
    fun checkLocationPermission(): Boolean
    fun finishActivity()
    fun requestLocationPermission(onLocationPermissionRequestResult: (Boolean) -> Unit)
}