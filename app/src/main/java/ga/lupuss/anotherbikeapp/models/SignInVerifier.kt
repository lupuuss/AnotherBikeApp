package ga.lupuss.anotherbikeapp.models

import ga.lupuss.anotherbikeapp.AnotherBikeApp
import ga.lupuss.anotherbikeapp.base.BaseActivity
import ga.lupuss.anotherbikeapp.base.BaseFragment
import ga.lupuss.anotherbikeapp.models.base.AuthInteractor
import ga.lupuss.anotherbikeapp.ui.modules.login.LoginActivity

class SignInVerifier(private val authInteractor: AuthInteractor) {

    fun verifySignedIn(baseActivity: BaseActivity): Boolean {

        val app = AnotherBikeApp.get(baseActivity.application)

        return if (authInteractor.isUserLogged()) {

            if (app.userComponent == null) {

                app.initUserComponent()
            }
            true
        } else {

            baseActivity.startActivity(LoginActivity.newIntent(baseActivity))
            baseActivity.finish()

            false
        }
    }

    fun verifySignedIn(baseFragment: BaseFragment): Boolean {

        val app = AnotherBikeApp.get(baseFragment.requireActivity().application)

        if (authInteractor.isUserLogged()) {

            if (app.userComponent == null) {

                app.initUserComponent()
            }
            return true
        }

        return false
    }
}