package ga.lupuss.anotherbikeapp.ui.modules.about

import dagger.Component
import ga.lupuss.anotherbikeapp.di.UserComponent
import javax.inject.Scope

@Scope
annotation class AboutAppComponentScope

@AboutAppComponentScope
@Component(dependencies = [UserComponent::class], modules = [AboutAppModule::class])
interface AboutAppComponent {

    fun inject(aboutAppActivity: AboutAppActivity)
}