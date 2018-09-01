package ga.lupuss.anotherbikeapp.ui.modules.forgotpassword

import ga.lupuss.anotherbikeapp.Message
import ga.lupuss.anotherbikeapp.base.BaseView

interface ForgotPasswordView : BaseView {

    fun emailFieldError(message: Message)
}