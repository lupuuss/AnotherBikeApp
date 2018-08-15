package ga.lupuss.anotherbikeapp.di

import android.support.v4.app.Fragment
import ga.lupuss.anotherbikeapp.Message
import ga.lupuss.anotherbikeapp.base.ThemedBaseActivity
import ga.lupuss.anotherbikeapp.base.BaseView
import ga.lupuss.anotherbikeapp.models.base.StringsResolver
import javax.inject.Inject

abstract class BaseFragment : Fragment(), BaseView {

    @Inject
    lateinit var stringsResolver: StringsResolver

    override fun postMessage(message: Message) {
        (activity as? ThemedBaseActivity)?.postMessage(message)
    }

    override fun makeToast(str: String) {
        (activity as? ThemedBaseActivity)?.makeToast(str)
    }

    override fun isOnline(): Boolean {
        return (activity as? ThemedBaseActivity)?.isOnline() ?: false
    }

    override fun checkLocationPermission(): Boolean {
        return  (activity as? ThemedBaseActivity)?.checkLocationPermission() ?: false
    }

    override fun finishActivity() {
        (activity as? ThemedBaseActivity)?.finishActivity()
    }

    override fun requestLocationPermission(onLocationPermissionRequestResult: (Boolean) -> Unit) {
        (activity as? ThemedBaseActivity)?.requestLocationPermission(onLocationPermissionRequestResult)
    }
}