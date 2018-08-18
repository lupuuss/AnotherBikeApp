package ga.lupuss.anotherbikeapp.di

import android.content.Context
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import dagger.Component
import ga.lupuss.anotherbikeapp.models.base.AuthInteractor
import ga.lupuss.anotherbikeapp.models.base.StringsResolver
import ga.lupuss.anotherbikeapp.models.weather.WeatherApi
import ga.lupuss.anotherbikeapp.ui.TrackingNotification
import java.util.*
import javax.inject.Scope

@Scope
annotation class AnotherBikeAppScope

@Component(modules = [AnotherBikeAppModule::class, AndroidModule::class, FirebaseModule::class])
@AnotherBikeAppScope
interface AnotherBikeAppComponent {

    fun providesGson(): Gson
    fun providesContext(): Context
    fun providesLocale(): Locale
    fun providesTimeProvider(): () -> Long
    fun providesTrackingNotification(): TrackingNotification

    fun providesStringResolver(): StringsResolver
    fun providesFirebaseFirestore(): FirebaseFirestore
    fun providesAuthInteractor(): AuthInteractor
    fun providesWeatherApi(): WeatherApi
}
