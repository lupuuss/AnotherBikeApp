package ga.lupuss.anotherbikeapp.di

import android.content.Context
import com.google.gson.Gson
import dagger.Component
import ga.lupuss.anotherbikeapp.models.base.*
import ga.lupuss.anotherbikeapp.ui.TrackingNotification
import java.util.*
import javax.inject.Scope

@Scope
annotation class UserComponentScope

@UserComponentScope
@Component(modules = [UserModule::class], dependencies = [AnotherBikeAppComponent::class])
interface UserComponent {

    fun providesGson(): Gson
    fun providesContext(): Context
    fun providesLocale(): Locale
    fun providesTimeProvider(): () -> Long
    fun providesTrackingNotification(): TrackingNotification
    fun providesStringResolver(): ResourceResolver
    fun providesAuthInteractor(): AuthInteractor

    fun providesRoutesManager(): RoutesManager
    fun providesPreferencesInteractor(): PreferencesInteractor
    fun providesWeatherManager(): WeatherManager
    fun providesLocalPhotosManager(): LocalPhotosManager
}
