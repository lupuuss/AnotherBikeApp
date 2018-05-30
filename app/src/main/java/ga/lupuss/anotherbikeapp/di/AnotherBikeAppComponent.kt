package ga.lupuss.anotherbikeapp.di

import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.auth.FirebaseAuth
import dagger.Component
import ga.lupuss.anotherbikeapp.models.routes.FirebaseRoutesManager
import java.util.*
import javax.inject.Scope

@Scope
annotation class AnotherBikeAppScope

@Component(modules = [AnotherBikeAppModule::class, CoreModule::class, MemoryModule::class, FirebaseModule::class])
@AnotherBikeAppScope
interface AnotherBikeAppComponent {

    fun providesContext(): Context
    fun providesLocale(): Locale
    fun providesTimeProvider(): () -> Long

    fun providesSharedPreferences(): SharedPreferences

    fun providesFirebaseAuth(): FirebaseAuth
    fun providesRoutesManager(): FirebaseRoutesManager
}
