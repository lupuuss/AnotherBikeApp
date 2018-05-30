package ga.lupuss.anotherbikeapp.base

interface BaseView {

    fun makeToast(stringId: Int)
    fun makeToast(str: String)
    fun isOnline(): Boolean
    fun finishActivity()
}