package ga.lupuss.anotherbikeapp.ui.modules.login

import android.content.Context
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

    @Inject
    lateinit var context: Context

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

                    initWithUser(User(it.user, context))

                }.addOnFailureListener {

                    fetchException(it)
                    Timber.d(it)
                }

    }

    fun fetchException(exception: Exception) {

        when (exception) {
        }

    }
}