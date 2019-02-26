package ga.lupuss.anotherbikeapp.ui.modules.forgotpassword

import dagger.Module
import dagger.Provides

@Module
class ForgotPasswordModule(forgotPasswordView: ForgotPasswordView) {

    val forgotPasswordView = forgotPasswordView
        @Provides
        @ForgotPasswordComponentScope
        get
}