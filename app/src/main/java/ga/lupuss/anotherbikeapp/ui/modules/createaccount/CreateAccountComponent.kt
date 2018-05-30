package ga.lupuss.anotherbikeapp.ui.modules.createaccount

import dagger.Component
import ga.lupuss.anotherbikeapp.di.AnotherBikeAppComponent
import javax.inject.Scope

@Scope
annotation class CreateAccountScope

@CreateAccountScope
@Component(modules = [CreateAccountModule::class], dependencies = [AnotherBikeAppComponent::class])
interface CreateAccountComponent {

    fun inject(createAccount: CreateAccountActivity)
}