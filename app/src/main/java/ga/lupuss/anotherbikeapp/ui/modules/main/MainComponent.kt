package ga.lupuss.anotherbikeapp.ui.modules.main

import com.google.gson.Gson
import dagger.Component
import ga.lupuss.anotherbikeapp.di.AnotherBikeAppComponent
import javax.inject.Scope

@Scope
annotation class MainComponentScope

@MainComponentScope
@Component(modules = [MainModule::class], dependencies = [AnotherBikeAppComponent::class])
interface MainComponent {

    fun inject(mainActivity: MainActivity)
}

