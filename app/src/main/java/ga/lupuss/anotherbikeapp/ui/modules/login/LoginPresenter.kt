package ga.lupuss.anotherbikeapp.ui.modules.login

import com.google.firebase.auth.FirebaseAuth
import ga.lupuss.anotherbikeapp.base.Presenter
import ga.lupuss.anotherbikeapp.models.User
import timber.log.Timber
import javax.inject.Inject

class LoginPresenter @Inject constructor() : Presenter {

    @Inject
    lateinit var loginView: LoginView

    @Inject
    lateinit var auth: FirebaseAuth

    fun initWithUser(user: User) {
        loginView.getAnotherBikeApp().initMainComponentWithUser(user)
        loginView.startMainActivity()
        loginView.finishActivity()
    }

    fun onClickUseWithoutAccount() {

    }

    fun onClickSignIn(email: String, password: String) {

        loginView.getAnotherBikeApp()
                .coreComponent
                .providesFirebaseAuth()
                .signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {

                    loginView.makeToast("No nieźle zalogowałeś się brawo kurwa :/")
                    initWithUser(User("test", it.user, false))

                }.addOnFailureListener {

                    Timber.d(it)
                    loginView.makeToast("Coś się popsuło i nie było mnie słychać :/")
                }

    }
}