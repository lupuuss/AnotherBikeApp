package ga.lupuss.anotherbikeapp.ui.modules.createaccount

import ga.lupuss.anotherbikeapp.base.BaseView


interface CreateAccountView : BaseView {
    var isUiEnable: Boolean
    var isCreateAccountProgressBarVisible: Boolean
    var isCreateAccountButtonTextVisible: Boolean

}