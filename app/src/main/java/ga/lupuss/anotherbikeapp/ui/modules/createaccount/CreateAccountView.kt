package ga.lupuss.anotherbikeapp.ui.modules.createaccount

import ga.lupuss.anotherbikeapp.Message
import ga.lupuss.anotherbikeapp.base.BaseView


interface CreateAccountView : BaseView {
    var isUiEnable: Boolean
    var isCreateAccountProgressBarVisible: Boolean
    var isCreateAccountButtonTextVisible: Boolean

    fun emailFieldError(message: Message)
    fun passwordFieldError(message: Message)
    fun displayNameFieldError(message: Message)
}