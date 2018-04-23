package ga.lupuss.anotherbikeapp.base

import android.os.Bundle

/** Base interface for presenters */
interface Presenter {

    fun notifyOnCreate(savedInstanceState: Bundle?) {}
    fun notifyOnDestroy(isFinishing: Boolean) {}
    fun notifyOnResult(requestCode: Int, resultCode: Int) {}
    fun notifyOnSavedInstanceState(outState: Bundle) {}
}