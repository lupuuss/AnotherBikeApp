package ga.lupuss.anotherbikeapp.models.firebase

interface AuthInteractor {

    interface OnLoginDoneListener {

        fun onSuccess()
        fun onCredentialsError()
        fun onUndefinedError()
    }

    fun login(email: String, password: String, onLoginDone: OnLoginDoneListener)
}