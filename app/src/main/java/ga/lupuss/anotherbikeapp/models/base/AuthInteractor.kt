package ga.lupuss.anotherbikeapp.models.base

interface AuthInteractor {

    interface OnAuthTaskDoneListener {
        fun onSuccess()
        fun onUndefinedError()
    }

    interface OnLoginDoneListener : OnAuthTaskDoneListener {
        fun onIncorrectCredentialsError()
        fun onUserNotExists()
    }

    interface OnAccountCreateDoneListener : OnAuthTaskDoneListener {
        fun onUserExist()
    }

    interface OnDisplayNameSetDoneListener : OnAuthTaskDoneListener

    fun login(email: String,
              password: String,
              onLoginDone: OnLoginDoneListener?,
              requestOwner: Any? = null)

    fun createAccount(email: String,
                      password: String,
                      displayName: String,
                      onCreateAccountDone: OnAccountCreateDoneListener?,
                      requestOwner: Any? = null)

    fun getEmail(): String?

    fun getDisplayName(): String?

    fun setDisplayName(displayName: String, onDisplayNameSetDone: OnDisplayNameSetDoneListener?)

    fun signOut()

    fun isUserLogged(): Boolean
}