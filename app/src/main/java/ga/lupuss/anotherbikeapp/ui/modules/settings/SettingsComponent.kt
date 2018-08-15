package ga.lupuss.anotherbikeapp.ui.modules.settings

import dagger.Component
import ga.lupuss.anotherbikeapp.di.AnotherBikeAppComponent
import ga.lupuss.anotherbikeapp.di.UserComponent
import javax.inject.Scope

@Scope
annotation class SettingsComponentScope

@SettingsComponentScope
@Component(dependencies = [UserComponent::class])
interface SettingsComponent {

    fun inject(settingsActivity: SettingsActivity)
}