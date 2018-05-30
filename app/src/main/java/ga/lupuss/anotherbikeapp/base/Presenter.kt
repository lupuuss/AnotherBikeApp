package ga.lupuss.anotherbikeapp.base


/** Base interface for presenters */
interface Presenter {

    fun initWithStateJson(stateJson: String?) {}

    fun notifyOnViewReady() {}

    fun notifyOnResult(requestCode: Int, resultCode: Int) {}

    fun notifyOnDestroy(isFinishing: Boolean) {}

    fun getStateJson(): String? = null
}