package ga.lupuss.anotherbikeapp.ui.modules.login

import android.util.Log
import dagger.Module
import dagger.Provides

@Module
class LoginModule(loginView: LoginView) {

    val loginView: LoginView = loginView
        @Provides
        @LoginScope
        get
}