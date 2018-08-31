package ga.lupuss.anotherbikeapp.di

import android.content.Context
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import ga.lupuss.anotherbikeapp.CACHE_SIZE
import ga.lupuss.anotherbikeapp.R
import ga.lupuss.anotherbikeapp.WEATHER_API_URL
import ga.lupuss.anotherbikeapp.models.firebase.FirebaseAuthInteractor
import ga.lupuss.anotherbikeapp.models.base.AuthInteractor
import ga.lupuss.anotherbikeapp.models.weather.OpenWeatherApi
import okhttp3.Cache
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.util.*

@Module()
class AnotherBikeAppModule {

    @Provides
    @AnotherBikeAppScope
    fun providesAuthInteractor(firebaseAuth: FirebaseAuth): AuthInteractor = FirebaseAuthInteractor(firebaseAuth)

    @Provides
    @AnotherBikeAppScope
    fun providesOkHttpClient(context: Context, locale: Locale): OkHttpClient {

        return OkHttpClient()
                .newBuilder()
                .cache(Cache(context.cacheDir, CACHE_SIZE))
                .addInterceptor { chain ->
                    val originalRequest = chain.request()

                    val url = originalRequest
                            .url()
                            .newBuilder()
                            .addQueryParameter("APPID", context.getString(R.string.open_weather_key))
                            .addQueryParameter("lang", locale.country)
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
    fun providesWeatherApi(gson: Gson, okHttpClient: OkHttpClient): OpenWeatherApi =
            Retrofit.Builder()
                    .baseUrl(WEATHER_API_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(okHttpClient)
                    .build()
                    .create(OpenWeatherApi::class.java)
}
