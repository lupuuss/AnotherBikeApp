package ga.lupuss.anotherbikeapp.di

import android.content.Context
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import ga.lupuss.anotherbikeapp.kotlin.SchedulersPackage
import ga.lupuss.anotherbikeapp.models.android.AndroidPathsGenerator
import ga.lupuss.anotherbikeapp.models.android.AndroidPreferencesInteractor
import ga.lupuss.anotherbikeapp.models.base.*
import ga.lupuss.anotherbikeapp.models.firebase.FirebasePhotosSynchronizer
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
                              @Suppress("SpellCheckingInspection") photosSynchronizer: PhotosSynchronizer,
                              gson: Gson): RoutesManager =
            FirebaseRoutesManager(authInteractor, firebaseFirestore, routesKeeper, locale, photosSynchronizer, gson)

    @Provides
    @UserComponentScope
    fun providesPreferencesInteractor(context: Context): PreferencesInteractor =
            AndroidPreferencesInteractor(context)

    @Provides
    @UserComponentScope
    fun providesWeatherManager(
            weatherApi: OpenWeatherApi, timeProvider: () -> Long, locale: Locale, schedulersPackage: SchedulersPackage
    ): WeatherManager = OpenWeatherManager(weatherApi, timeProvider, locale, schedulersPackage)


    @Provides
    @UserComponentScope
    fun providesPhotosSynchronizer(storage: FirebaseStorage,
                                   pathsGenerator: PathsGenerator,
                                   gson: Gson): PhotosSynchronizer =
            FirebasePhotosSynchronizer(storage, pathsGenerator, gson)

    @Provides
    @UserComponentScope
    fun providesPathsGenerator(context: Context,
                               authInteractor: AuthInteractor,
                               timeProvider: () -> Long): PathsGenerator =
            AndroidPathsGenerator(context, authInteractor, timeProvider)
}