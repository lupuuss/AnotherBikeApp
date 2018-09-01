package ga.lupuss.anotherbikeapp.ui.modules.forgotpassword

import dagger.Component
import ga.lupuss.anotherbikeapp.di.AnotherBikeAppComponent
import javax.inject.Scope

@Scope
annotation class ForgotPasswordComponentScope

@ForgotPasswordComponentScope
@Component(dependencies = [AnotherBikeAppComponent::class], modules = [ForgotPasswordModule::class])
interface ForgotPasswordComponent {

    fun inject(forgotPasswordActivity: ForgotPasswordActivity)
}