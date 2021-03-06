package ga.lupuss.anotherbikeapp.ui.modules.weather

import dagger.Module
import dagger.Provides

@Module
class WeatherModule(weatherView: WeatherView) {

    val weatherView: WeatherView = weatherView
        @Provides
        @WeatherComponentScope
        get

}