package ga.lupuss.anotherbikeapp.base


/** Base interface for presenters */
interface Presenter {

    fun notifyOnViewReady() {}
    fun notifyOnResult(requestCode: Int, resultCode: Int) {}
    fun notifyOnDestroy(isFinishing: Boolean) {}

}