package ga.lupuss.anotherbikeapp.models.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import javax.inject.Inject

class FirebaseAuthInteractor @Inject constructor(private val firebaseAuth: FirebaseAuth) : AuthInteractor {

    override fun login(email: String, password: String, onLoginDone: AuthInteractor.OnLoginDoneListener) {

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    onLoginDone.onSuccess()
                }
                .addOnFailureListener {

                    when (it) {
                        is FirebaseAuthInvalidCredentialsException -> onLoginDone.onCredentialsError()
                        else -> onLoginDone.onUndefinedError()
                    }
                }

    }

    override fun getEmail(): String? = firebaseAuth.currentUser?.email

    override fun getDisplayName(): String? = firebaseAuth.currentUser?.displayName
}