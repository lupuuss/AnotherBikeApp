package ga.lupuss.anotherbikeapp.models

import ga.lupuss.anotherbikeapp.AnotherBikeApp
import ga.lupuss.anotherbikeapp.base.BaseActivity
import ga.lupuss.anotherbikeapp.base.BaseFragment
import ga.lupuss.anotherbikeapp.models.base.AuthInteractor
import ga.lupuss.anotherbikeapp.ui.modules.login.LoginActivity

class SignInVerifier(private val authInteractor: AuthInteractor) {

    fun verifySignedIn(baseActivity: BaseActivity): Boolean {

        val app = AnotherBikeApp.get(baseActivity.application)

        if (authInteractor.isUserLogged()) {

            if (app.userComponent == null) {

                app.initUserComponent()
            }
            return true
        } else {

            baseActivity.startActivity(LoginActivity.newIntent(baseActivity))
            baseActivity.finish()

            return false
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