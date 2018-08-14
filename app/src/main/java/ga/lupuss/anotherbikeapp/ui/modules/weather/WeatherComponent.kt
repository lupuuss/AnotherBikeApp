package ga.lupuss.anotherbikeapp.ui.modules.weather

import dagger.Component
import ga.lupuss.anotherbikeapp.di.AnotherBikeAppComponent
import javax.inject.Scope


@Scope
annotation class WeatherComponentScope

@WeatherComponentScope
@Component(dependencies = [AnotherBikeAppComponent::class], modules = [WeatherModule::class])
interface WeatherComponent {

    fun inject(weatherFragment: WeatherFragment)
}