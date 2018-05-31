package ga.lupuss.anotherbikeapp.models.interfaces

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

    fun login(email: String, password: String, onLoginDone: OnLoginDoneListener?)

    fun createAccount(email: String,
                      password: String,
                      displayName: String,
                      onCreateAccountDone: OnAccountCreateDoneListener?)

    fun getEmail(): String?

    fun getDisplayName(): String?

    fun setDisplayName(displayName: String, onDisplayNameSetDone: OnDisplayNameSetDoneListener?)

    fun signOut()
}