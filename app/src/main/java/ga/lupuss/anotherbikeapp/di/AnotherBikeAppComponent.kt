package ga.lupuss.anotherbikeapp.di

import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import dagger.Component
import ga.lupuss.anotherbikeapp.models.android.AndroidStringsResolver
import ga.lupuss.anotherbikeapp.models.routes.FirebaseRoutesManager
import java.util.*
import javax.inject.Scope

@Scope
annotation class AnotherBikeAppScope

@Component(modules = [AnotherBikeAppModule::class, AndroidModule::class, MemoryModule::class, FirebaseModule::class])
@AnotherBikeAppScope
interface AnotherBikeAppComponent {

    fun providesGson(): Gson
    fun providesContext(): Context
    fun providesLocale(): Locale
    fun providesTimeProvider(): () -> Long

    fun providesSharedPreferences(): SharedPreferences

    fun providesFirebaseAuth(): FirebaseAuth
    fun providesRoutesManager(): FirebaseRoutesManager
    fun providesAndroidStringResolver(): AndroidStringsResolver
}
