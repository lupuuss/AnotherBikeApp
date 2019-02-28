package ga.lupuss.anotherbikeapp.base

import android.content.Context
import android.location.Location
import android.os.Bundle
import androidx.fragment.app.Fragment
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

    override fun onStop() {
        super.onStop()

        if (verificationPassed || !requiresPassedVerification) {

            onStopPostVerification()
        }
    }

    open fun onStopPostVerification() {}

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

    override fun startMainActivity() {
        (activity as? BaseActivity)?.startMainActivity()
    }

    override fun startCreateAccountActivity() {
        (activity as? BaseActivity)?.startCreateAccountActivity()
    }

    override fun startSettingsActivity() {
        (activity as? BaseActivity)?.startSettingsActivity()
    }

    override fun startLoginActivity() {
        (activity as? BaseActivity)?.startLoginActivity()
    }

    override fun startForgotPasswordActivity() {
        (activity as? BaseActivity)?.startForgotPasswordActivity()
    }

    override fun startAboutAppActivity() {
        (activity as? BaseActivity)?.startAboutAppActivity()
    }

    override fun startSummaryActivity() {
        (activity as? BaseActivity)?.startSummaryActivity()
    }

    override fun startSummaryActivity(documentReference: String) {
        (activity as? BaseActivity)?.startSummaryActivity(documentReference)
    }

    override fun requestPhoto(photoRequest: BaseView.PhotoRequest) {
        (activity as? BaseActivity)?.requestPhoto(photoRequest)
    }
}