package ga.lupuss.anotherbikeapp.di

import android.content.Context
import android.content.SharedPreferences
import dagger.Component
import ga.lupuss.anotherbikeapp.models.FileObserverFactory
import ga.lupuss.anotherbikeapp.models.RoutesManager
import java.util.*
import javax.inject.Scope

@Scope
annotation class AnotherBikeAppScope

@Component(modules = [UserModule::class, AnotherBikeAppModule::class], dependencies = [CoreComponent::class])
@AnotherBikeAppScope
interface AnotherBikeAppComponent {

    // CoreComponent
    fun providesContext(): Context

    fun providesLocale(): Locale
    fun providesTimeProvider(): () -> Long
    fun providesSharedPreferences(): SharedPreferences
    fun providesFileObserverFactory(): FileObserverFactory

    // AnotherBikeAppComponent
    fun providesRoutesKeeper(): RoutesManager
}
