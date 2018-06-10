package ga.lupuss.anotherbikeapp.base

import ga.lupuss.anotherbikeapp.kotlin.Resettable
import ga.lupuss.anotherbikeapp.kotlin.ResettableManager


/** Base interface for presenters */
abstract class Presenter<T : BaseView> {

    private val resettableManager = ResettableManager()
    var view: T by Resettable(resettableManager)

    open fun notifyOnViewReady() {}
    open fun notifyOnResult(requestCode: Int, resultCode: Int) {}
    open fun notifyOnDestroy(isFinishing: Boolean) {
        resettableManager.reset()
    }

}