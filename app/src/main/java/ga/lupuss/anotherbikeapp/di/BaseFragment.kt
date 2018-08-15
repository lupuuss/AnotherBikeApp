package ga.lupuss.anotherbikeapp.di

import android.content.Context
import android.support.v4.app.Fragment
import ga.lupuss.anotherbikeapp.Message
import ga.lupuss.anotherbikeapp.base.BaseActivity
import ga.lupuss.anotherbikeapp.base.BaseView
import ga.lupuss.anotherbikeapp.models.base.StringsResolver
import javax.inject.Inject

abstract class BaseFragment : Fragment(), BaseView {

    @Inject
    lateinit var stringsResolver: StringsResolver

    override fun postMessage(message: Message) {
        (activity as? BaseActivity)?.postMessage(message)
    }

    override fun makeToast(str: String) {
        (activity as? BaseActivity)?.makeToast(str)
    }

    override fun isOnline(): Boolean {
        return (activity as? BaseActivity)?.isOnline() ?: false
    }

    override fun checkLocationPermission(): Boolean {
        return  (activity as? BaseActivity)?.checkLocationPermission() ?: false
    }

    override fun finishActivity() {
        (activity as? BaseActivity)?.finishActivity()
    }

    override fun requestLocationPermission(onLocationPermissionRequestResult: (Boolean) -> Unit) {
        (activity as? BaseActivity)?.requestLocationPermission(onLocationPermissionRequestResult)
    }
}