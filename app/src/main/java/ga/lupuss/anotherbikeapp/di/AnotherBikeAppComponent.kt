package ga.lupuss.anotherbikeapp.di

import android.content.Context
import android.content.SharedPreferences
import dagger.Component
import ga.lupuss.anotherbikeapp.models.RoutesManager
import ga.lupuss.anotherbikeapp.models.pojo.User
import java.util.*
import javax.inject.Scope

@Scope
annotation class AnotherBikeAppScope

@Component(modules = [AnotherBikeAppModule::class, MemoryModule::class, UserModule::class])
@AnotherBikeAppScope
interface AnotherBikeAppComponent {

    fun providesContext(): Context
    fun providesLocale(): Locale
    fun providesRoutesKeeper(): RoutesManager
    fun providesCurrentUser(): User
    fun providesTimeProvider(): () -> Long
    fun providesSharedPreferences(): SharedPreferences
}
