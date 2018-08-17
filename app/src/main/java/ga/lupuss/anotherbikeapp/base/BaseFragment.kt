package ga.lupuss.anotherbikeapp.base

import android.location.Location
import android.support.v4.app.Fragment
import ga.lupuss.anotherbikeapp.Message
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

    override fun finishActivity() {
        (activity as? ThemedBaseActivity)?.finishActivity()
    }

    override fun provideLocationPermission(onLocationPermissionRequestResult: (Boolean) -> Unit) {
        (activity as? ThemedBaseActivity)?.provideLocationPermission(onLocationPermissionRequestResult)
    }

    override fun provideLocationPermission(onLocationPermissionGranted: () -> Unit, onLocationPermissionRefused: () -> Unit) {
        (activity as? ThemedBaseActivity)?.provideLocationPermission(onLocationPermissionGranted, onLocationPermissionRefused)
    }

    override fun requestSingleLocationUpdate(onComplete: (Boolean, Location?) -> Unit) {

        (activity as? ThemedBaseActivity)?.requestSingleLocationUpdate(onComplete)
    }
}