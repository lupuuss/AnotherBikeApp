package ga.lupuss.anotherbikeapp.ui.modules.weather

import dagger.Component
import ga.lupuss.anotherbikeapp.di.UserComponent
import javax.inject.Scope


@Scope
annotation class WeatherComponentScope

@WeatherComponentScope
@Component(dependencies = [UserComponent::class], modules = [WeatherModule::class])
interface WeatherComponent {

    fun inject(weatherFragment: WeatherFragment)
}