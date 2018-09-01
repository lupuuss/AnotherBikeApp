package ga.lupuss.anotherbikeapp.base

import android.content.Context
import android.location.Location
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.View
import ga.lupuss.anotherbikeapp.AnotherBikeApp
import ga.lupuss.anotherbikeapp.Message
import ga.lupuss.anotherbikeapp.models.SignInVerifier
import ga.lupuss.anotherbikeapp.models.base.ResourceResolver
import javax.inject.Inject

abstract class BaseFragment : Fragment(), BaseView {

    @Inject
    lateinit var resourceResolver: ResourceResolver
    private lateinit var signInVerifier: SignInVerifier
    private var requiresPassedVerification = false
    private var verificationPassed = false

    fun requiresVerification() {

        requiresPassedVerification = true
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        signInVerifier = AnotherBikeApp.get(requireActivity().application).signInVerifier

        if (requiresPassedVerification) {

            verificationPassed = signInVerifier.verifySignedIn(this)
        }


        if (verificationPassed || !requiresPassedVerification) {

            onAttachPostVerification(context)
        }
    }

    open fun onAttachPostVerification(context: Context?) {}

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (verificationPassed || !requiresPassedVerification) {

            onViewCreatedPostVerification(view, savedInstanceState)
        }
    }

    open fun onViewCreatedPostVerification(view: View, savedInstanceState: Bundle?) {}

    override fun onStart() {
        super.onStart()

        if (verificationPassed || !requiresPassedVerification) {

            onStartPostVerification()
        }
    }

    open fun onStartPostVerification() {}

    override fun onDestroyView() {
        super.onDestroyView()

        if (verificationPassed || !requiresPassedVerification) {

            onDestroyViewPostVerification()
        }
    }

    open fun onDestroyViewPostVerification() {}

    override fun postMessage(message: Message) {
        (activity as? BaseActivity)?.postMessage(message)
    }

    override fun makeToast(str: String) {
        (activity as? BaseActivity)?.makeToast(str)
    }

    override fun isOnline(): Boolean {
        return (activity as? BaseActivity)?.isOnline() ?: false
    }

    override fun finishActivity() {
        (activity as? BaseActivity)?.finishActivity()
    }

    override fun provideLocationPermission(onLocationPermissionRequestResult: (Boolean) -> Unit) {
        (activity as? BaseActivity)?.provideLocationPermission(onLocationPermissionRequestResult)
    }

    override fun provideLocationPermission(onLocationPermissionGranted: () -> Unit, onLocationPermissionRefused: () -> Unit) {
        (activity as? BaseActivity)?.provideLocationPermission(onLocationPermissionGranted, onLocationPermissionRefused)
    }

    override fun requestSingleLocationUpdate(onComplete: (Boolean, Location?) -> Unit) {

        (activity as? BaseActivity)?.requestSingleLocationUpdate(onComplete)
    }

    override fun redirectToUrl(url: String) {
        (activity as? BaseActivity)?.redirectToUrl(url)
    }
}