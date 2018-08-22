package ga.lupuss.anotherbikeapp.ui.modules.main

import dagger.Component
import ga.lupuss.anotherbikeapp.di.UserComponent
import javax.inject.Scope

@Scope
annotation class MainComponentScope

@MainComponentScope
@Component(modules = [MainModule::class], dependencies = [UserComponent::class])
interface MainComponent {

    fun inject(mainActivity: MainActivity)
}

