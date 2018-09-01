package ga.lupuss.anotherbikeapp.models.firebase

import android.app.Activity
import com.google.firebase.auth.*
import ga.lupuss.anotherbikeapp.models.base.AuthInteractor
import timber.log.Timber

class FirebaseAuthInteractor(
        private val firebaseAuth: FirebaseAuth,
        private val userProfileChangeBuilder :UserProfileChangeRequest.Builder = UserProfileChangeRequest.Builder()
) : AuthInteractor {

    init {

        firebaseAuth.useAppLanguage()
    }

    override val userUid
        get() = firebaseAuth.currentUser?.uid

    override fun login(email: String, password: String, onLoginDone: AuthInteractor.OnLoginDoneListener?, requestOwner: Any?) {

        if (requestOwner !is Activity)
            throw IllegalArgumentException(FirebaseRoutesManager.WRONG_OWNER)

        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(requestOwner) {
                    onLoginDone?.onSuccess()
                }
                .addOnFailureListener(requestOwner) {

                    Timber.e(it)

                    when (it) {
                        is FirebaseAuthInvalidCredentialsException -> onLoginDone?.onInvalidCredentialsError()
                        is FirebaseAuthInvalidUserException -> onLoginDone?.onUserNotExists()
                        else -> onLoginDone?.onUndefinedError()
                    }
                }

    }

    override fun getEmail(): String? = firebaseAuth.currentUser?.email

    override fun getDisplayName(): String? = firebaseAuth.currentUser?.displayName

    override fun createAccount(email: String,
                               password: String,
                               displayName: String,
                               onCreateAccountDone: AuthInteractor.OnAccountCreateDoneListener?,
                               requestOwner: Any?) {
        if (requestOwner !is Activity)
            throw IllegalArgumentException(FirebaseRoutesManager.WRONG_OWNER)

        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(requestOwner) {

                    onCreateAccountDone?.onSuccess()
                    setDisplayName(displayName, null, requestOwner)
                }
                .addOnFailureListener(requestOwner) {

                    Timber.e(it)

                    when (it) {
                        is FirebaseAuthWeakPasswordException -> onCreateAccountDone?.onTooWeakPassword()
                        is FirebaseAuthInvalidCredentialsException -> onCreateAccountDone?.onInvalidCredentialsError()
                        is FirebaseAuthUserCollisionException -> onCreateAccountDone?.onUserExist()
                        else -> onCreateAccountDone?.onUndefinedError()
                    }
                }
    }

    override fun resetPassword(email: String, onPasswordResetDone: AuthInteractor.OnPasswordResetDoneListener?, requestOwner: Any?) {
        if (requestOwner !is Activity)
            throw IllegalArgumentException(FirebaseRoutesManager.WRONG_OWNER)

        firebaseAuth
                .sendPasswordResetEmail(email)
                .addOnSuccessListener(requestOwner) {

                    onPasswordResetDone?.onSuccess()
                }
                .addOnFailureListener(requestOwner) {

                    Timber.e(it)

                    when (it) {
                        is FirebaseAuthInvalidUserException -> onPasswordResetDone?.onUserNotExists()
                        is FirebaseAuthInvalidCredentialsException -> onPasswordResetDone?.onEmailBadlyFormatted()
                        else -> onPasswordResetDone?.onUndefinedError()
                    }
                }
    }

    override fun setDisplayName(displayName: String,
                                onDisplayNameSetDone: AuthInteractor.OnDisplayNameSetDoneListener?,
                                requestOwner: Any?) {

        if (requestOwner !is Activity)
            throw IllegalArgumentException(FirebaseRoutesManager.WRONG_OWNER)

        val userProfileChangeRequest = userProfileChangeBuilder
                .setDisplayName(displayName)
                .build()

        firebaseAuth.currentUser!!.updateProfile(userProfileChangeRequest)
                .addOnSuccessListener(requestOwner) {
                    onDisplayNameSetDone?.onSuccessNameChange()
                }
                .addOnFailureListener(requestOwner) {
                    Timber.e(it)
                    onDisplayNameSetDone?.onSettingDisplayNameFail()
                }
    }

    override fun signOut() {

        firebaseAuth.signOut()
    }

    override fun isUserLogged(): Boolean = firebaseAuth.currentUser != null
}