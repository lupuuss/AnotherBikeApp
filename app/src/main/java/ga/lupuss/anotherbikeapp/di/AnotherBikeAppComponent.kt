package ga.lupuss.anotherbikeapp.di

import android.content.Context
import dagger.Component
import ga.lupuss.anotherbikeapp.models.RoutesKeeper
import java.util.*
import javax.inject.Scope

@Scope
annotation class AnotherBikeAppScope

@Component(modules = [AnotherBikeAppModule::class, MemoryModule::class])
@AnotherBikeAppScope
interface AnotherBikeAppComponent {

    fun providesContext(): Context
    fun providesLocale(): Locale
    fun providesRoutesKeeper(): RoutesKeeper
}
