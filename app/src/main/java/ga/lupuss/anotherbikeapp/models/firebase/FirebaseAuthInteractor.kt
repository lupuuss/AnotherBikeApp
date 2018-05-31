package ga.lupuss.anotherbikeapp.models.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.UserProfileChangeRequest
import ga.lupuss.anotherbikeapp.models.interfaces.AuthInteractor
import timber.log.Timber

class FirebaseAuthInteractor(private val firebaseAuth: FirebaseAuth) : AuthInteractor {

    override fun login(email: String, password: String, onLoginDone: AuthInteractor.OnLoginDoneListener?) {

        Timber.d("Login with => $email : $password")

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener {
                    onLoginDone?.onSuccess()
                }
                .addOnFailureListener {

                    Timber.d(it)

                    when (it) {
                        is FirebaseAuthInvalidCredentialsException -> onLoginDone?.onIncorrectCredentialsError()
                        else -> onLoginDone?.onUndefinedError()
                    }
                }

    }

    override fun getEmail(): String? = firebaseAuth.currentUser?.email

    override fun getDisplayName(): String? = firebaseAuth.currentUser?.displayName

    override fun createAccount(email: String,
                               password: String,
                               displayName: String,
                               onCreateAccountDone: AuthInteractor.OnAccountCreateDoneListener?) {

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener {

                    onCreateAccountDone?.onSuccess()
                    setDisplayName(displayName, null)
                }
    }

    override fun setDisplayName(displayName: String,
                                onDisplayNameSetDone: AuthInteractor.OnDisplayNameSetDoneListener?) {

        val userProfileChangeRequest = UserProfileChangeRequest
                .Builder()
                .setDisplayName(displayName)
                .build()

        firebaseAuth.currentUser?.updateProfile(userProfileChangeRequest)
                ?: onDisplayNameSetDone?.onUndefinedError()
    }

    override fun signOut() {

        firebaseAuth.signOut()
    }
}