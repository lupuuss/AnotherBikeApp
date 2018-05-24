package ga.lupuss.anotherbikeapp.ui.modules.login

import dagger.Component
import ga.lupuss.anotherbikeapp.di.CoreComponent
import javax.inject.Scope

@Scope
annotation class LoginScope

@LoginScope
@Component(modules = [LoginModule::class], dependencies = [CoreComponent::class])
interface LoginComponent {

    fun inject(loginActivity: LoginActivity)
}