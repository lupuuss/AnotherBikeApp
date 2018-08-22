package ga.lupuss.anotherbikeapp.di

import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import ga.lupuss.anotherbikeapp.models.android.AndroidPreferencesInteractor
import ga.lupuss.anotherbikeapp.models.base.AuthInteractor
import ga.lupuss.anotherbikeapp.models.base.PreferencesInteractor
import ga.lupuss.anotherbikeapp.models.base.RoutesManager
import ga.lupuss.anotherbikeapp.models.firebase.FirebaseRoutesManager
import ga.lupuss.anotherbikeapp.models.firebase.TempRouteKeeper
import ga.lupuss.anotherbikeapp.models.weather.WeatherApi
import ga.lupuss.anotherbikeapp.models.weather.WeatherManager
import java.util.*

@Module
class UserModule {

    @Provides
    @UserComponentScope
    fun providesRoutesManager(authInteractor: AuthInteractor,
                              routesKeeper: TempRouteKeeper,
                              firebaseFirestore: FirebaseFirestore,
                              locale: Locale,
                              gson: Gson): RoutesManager =
            FirebaseRoutesManager(authInteractor, firebaseFirestore, routesKeeper, locale, gson)

    @Provides
    @UserComponentScope
    fun providesPreferencesInteractor(context: Context): PreferencesInteractor =
            AndroidPreferencesInteractor(context)

    @Provides
    @UserComponentScope
    fun providesWeatherManager(weatherApi: WeatherApi, timeProvider: () -> Long, locale: Locale) =
            WeatherManager(weatherApi, timeProvider, locale)
}