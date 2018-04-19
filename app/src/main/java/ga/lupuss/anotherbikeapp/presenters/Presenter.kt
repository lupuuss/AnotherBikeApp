package ga.lupuss.anotherbikeapp.presenters

/** Base interface for presenters */
interface Presenter {

    /** Base interface for every Presenter.IView e.g [MainPresenter.IView]*/
    interface BaseView {
        fun makeToast(stringId: Int)
        fun makeToast(str: String)
    }

    fun notifyOnCreate() {}
    fun notifyOnDestroy(isFinishing: Boolean) {}
    fun notifyOnResult(requestCode: Int, resultCode: Int) {}
}