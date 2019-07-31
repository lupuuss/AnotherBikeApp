package ga.lupuss.anotherbikeapp.di

import android.content.Context
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.gson.Gson
import dagger.Component
import ga.lupuss.anotherbikeapp.kotlin.SchedulersPackage
import ga.lupuss.anotherbikeapp.models.base.AuthInteractor
import ga.lupuss.anotherbikeapp.models.FilesWrapper
import ga.lupuss.anotherbikeapp.models.base.ResourceResolver
import ga.lupuss.anotherbikeapp.models.weather.OpenWeatherApi
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

    fun providesResourceResolver(): ResourceResolver
    fun providesFirebaseFirestore(): FirebaseFirestore
    fun providesAuthInteractor(): AuthInteractor
    fun providesWeatherApi(): OpenWeatherApi
    fun providesSchedulers(): SchedulersPackage
    fun providesFirebaseStorage(): FirebaseStorage
    fun providesFilesWrapper(): FilesWrapper
}
