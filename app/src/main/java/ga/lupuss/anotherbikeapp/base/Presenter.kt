package ga.lupuss.anotherbikeapp.base

import android.os.Bundle

/** Base interface for presenters */
interface Presenter {

    /** Base interface for every Presenter.IView e.g
     * [ga.lupuss.anotherbikeapp.ui.modules.main.MainPresenter.IView]*/
    interface BaseView {
        fun makeToast(stringId: Int)
        fun makeToast(str: String)
    }

    fun notifyOnCreate(savedInstanceState: Bundle?) {}
    fun notifyOnDestroy(isFinishing: Boolean) {}
    fun notifyOnResult(requestCode: Int, resultCode: Int) {}
    fun notifyOnSavedInstanceState(outState: Bundle) {}
}