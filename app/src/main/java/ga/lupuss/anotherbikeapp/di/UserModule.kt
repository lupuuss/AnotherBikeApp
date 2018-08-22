package ga.lupuss.anotherbikeapp.di

import android.content.Context
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import ga.lupuss.anotherbikeapp.kotlin.SchedulersPackage
import ga.lupuss.anotherbikeapp.models.android.AndroidPreferencesInteractor
import ga.lupuss.anotherbikeapp.models.base.AuthInteractor
import ga.lupuss.anotherbikeapp.models.base.PreferencesInteractor
import ga.lupuss.anotherbikeapp.models.base.RoutesManager
import ga.lupuss.anotherbikeapp.models.base.WeatherManager
import ga.lupuss.anotherbikeapp.models.firebase.FirebaseRoutesManager
import ga.lupuss.anotherbikeapp.models.firebase.TempRouteKeeper
import ga.lupuss.anotherbikeapp.models.weather.OpenWeatherApi
import ga.lupuss.anotherbikeapp.models.weather.OpenWeatherManager
import java.util.*

@Module
class UserModule {

    @Provides
    @UserComponentScope
    fun providesRoutesManager(authInteractor: AuthInteractor,
                              routesKeeper: TempRouteKeeper,
                              firebaseFirestore: FirebaseFirestore,
                              locale: Locale,
                              gson: Gson,
                              schedulersPackage: SchedulersPackage): RoutesManager =
            FirebaseRoutesManager(authInteractor, firebaseFirestore, routesKeeper, locale, gson, schedulersPackage)

    @Provides
    @UserComponentScope
    fun providesPreferencesInteractor(context: Context): PreferencesInteractor =
            AndroidPreferencesInteractor(context)

    @Provides
    @UserComponentScope
    fun providesWeatherManager(
            weatherApi: OpenWeatherApi, timeProvider: () -> Long, locale: Locale, schedulersPackage: SchedulersPackage
    ): WeatherManager = OpenWeatherManager(weatherApi, timeProvider, locale, schedulersPackage)
}