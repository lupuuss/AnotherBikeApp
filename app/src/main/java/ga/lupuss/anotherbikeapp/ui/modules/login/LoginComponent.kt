package ga.lupuss.anotherbikeapp.ui.modules.login

import dagger.Component
import ga.lupuss.anotherbikeapp.di.AnotherBikeAppComponent
import javax.inject.Scope

@Scope
annotation class LoginScope

@LoginScope
@Component(modules = [LoginModule::class], dependencies = [AnotherBikeAppComponent::class])
interface LoginComponent {

    fun inject(loginActivity: LoginActivity)
}