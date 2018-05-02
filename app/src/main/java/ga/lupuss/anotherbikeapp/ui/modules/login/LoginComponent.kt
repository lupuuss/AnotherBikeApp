package ga.lupuss.anotherbikeapp.ui.modules.login

import dagger.Component
import javax.inject.Scope

@Scope
annotation class LoginScope

@LoginScope
@Component(modules = [LoginModule::class])
interface LoginComponent {

    fun inject(loginActivity: LoginActivity)
}