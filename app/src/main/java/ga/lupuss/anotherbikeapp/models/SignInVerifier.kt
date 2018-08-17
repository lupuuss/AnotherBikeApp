package ga.lupuss.anotherbikeapp.models

import ga.lupuss.anotherbikeapp.AnotherBikeApp
import ga.lupuss.anotherbikeapp.base.BaseActivity
import ga.lupuss.anotherbikeapp.models.base.AuthInteractor

class SignInVerifier(private val authInteractor: AuthInteractor) {

    fun verifySignedIn(baseActivity: BaseActivity) {

        val app = AnotherBikeApp.get(baseActivity.application)

        if (authInteractor.isUserLogged()) {

            if (app.userComponent == null) {

                app.initUserModule()
            }
        }
    }
}