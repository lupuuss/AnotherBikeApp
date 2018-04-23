package ga.lupuss.anotherbikeapp.ui.modules.main

import dagger.Component
import javax.inject.Scope

@Scope
annotation class MainComponentScope

@MainComponentScope
@Component(modules = [MainModule::class])
interface MainComponent {
    fun inject(mainActivity: MainActivity)
}

