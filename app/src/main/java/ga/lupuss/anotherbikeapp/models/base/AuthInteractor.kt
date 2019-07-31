package ga.lupuss.anotherbikeapp.models.base

interface AuthInteractor {

    interface OnCompleteListener {
        fun onSuccess()
        fun onUndefinedError()
    }

    interface OnAuthTaskDoneListener : OnCompleteListener {
        fun onInvalidCredentialsError()
    }

    interface OnPasswordResetDoneListener : OnCompleteListener {

        fun onEmailBadlyFormatted()
        fun onUserNotExists()
    }

    interface OnLoginDoneListener : OnAuthTaskDoneListener {
        fun onUserNotExists()
    }

    interface OnAccountCreationDoneListener : OnAuthTaskDoneListener {
        fun onUserExist()
        fun onTooWeakPassword()
    }

    interface OnDisplayNameSetDoneListener {
        fun onFailSettingDisplayName()
        fun onSuccessSettingDisplayName()
    }

    val userUid: String?

    val email: String?

    val displayName: String?

    fun login(email: String,
              password: String,
              onLoginDone: OnLoginDoneListener?,
              requestOwner: Any? = null)

    fun createAccount(email: String,
                      password: String,
                      displayName: String,
                      onCreateAccountDone: OnAccountCreationDoneListener?,
                      requestOwner: Any? = null)

    fun resetPassword(email: String,
                      onPasswordResetDone: OnPasswordResetDoneListener?,
                      requestOwner: Any? = null)

    fun setDisplayName(displayName: String, onDisplayNameSetDone: OnDisplayNameSetDoneListener?, requestOwner: Any? = null)

    fun signOut()

    fun isUserLogged(): Boolean
}