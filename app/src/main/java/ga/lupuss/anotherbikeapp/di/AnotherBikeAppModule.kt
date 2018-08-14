package ga.lupuss.anotherbikeapp.di

import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import ga.lupuss.anotherbikeapp.R
import ga.lupuss.anotherbikeapp.WEATHER_API_URL
import ga.lupuss.anotherbikeapp.models.android.AndroidPreferencesInteractor
import ga.lupuss.anotherbikeapp.models.firebase.FirebaseRoutesManager
import ga.lupuss.anotherbikeapp.models.firebase.TempRouteKeeper
import ga.lupuss.anotherbikeapp.models.firebase.FirebaseAuthInteractor
import ga.lupuss.anotherbikeapp.models.base.AuthInteractor
import ga.lupuss.anotherbikeapp.models.base.PreferencesInteractor
import ga.lupuss.anotherbikeapp.models.base.RoutesManager
import ga.lupuss.anotherbikeapp.models.weather.WeatherApi
import ga.lupuss.anotherbikeapp.models.weather.WeatherManager
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

@Module()
class AnotherBikeAppModule {

    @Provides
    @AnotherBikeAppScope
    fun providesRoutesManager(firebaseAuth: FirebaseAuth,
                              routesKeeper: TempRouteKeeper,
                              firebaseFirestore: FirebaseFirestore,
                              locale: Locale,
                              gson: Gson): RoutesManager =
            FirebaseRoutesManager(firebaseAuth, firebaseFirestore, routesKeeper, locale, gson)

    @Provides
    @AnotherBikeAppScope
    fun providesAuthInteractor(firebaseAuth: FirebaseAuth): AuthInteractor = FirebaseAuthInteractor(firebaseAuth)

    @Provides
    @AnotherBikeAppScope
    fun providesPreferencesInteractor(sharedPreferences: SharedPreferences, context: Context): PreferencesInteractor =
            AndroidPreferencesInteractor(sharedPreferences, context)

    @Provides
    @AnotherBikeAppScope
    fun providesOkHttpClient(context: Context, locale: Locale): OkHttpClient {

        return OkHttpClient()
                .newBuilder()
                .addInterceptor { chain ->
                    val originalRequest = chain.request()

                    val url = originalRequest
                            .url()
                            .newBuilder()
                            .addQueryParameter("APPID", context.getString(R.string.open_weather_key))
                            .addQueryParameter("lang", locale.country)
                            .addQueryParameter("units", "metric")
                            .build()

                    val builder = originalRequest
                            .newBuilder()
                            .url(url)

                    val newRequest = builder.build()
                    chain.proceed(newRequest)
                }
                .build()
    }

    @Provides
    @AnotherBikeAppScope
    fun providesWeatherApi(gson: Gson, okHttpClient: OkHttpClient): WeatherApi =
            Retrofit.Builder()
                    .baseUrl(WEATHER_API_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(okHttpClient)
                    .build()
                    .create(WeatherApi::class.java)

    @Provides
    @AnotherBikeAppScope
    fun providesWeatherManager(weatherApi: WeatherApi, timeProvider: () -> Long) =
            WeatherManager(weatherApi, timeProvider)
}
