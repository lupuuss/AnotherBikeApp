package ga.lupuss.anotherbikeapp.di

import android.content.Context
import dagger.Component
import javax.inject.Scope

@Scope
annotation class AnotherBikeAppScope

@Component(modules = [AnotherBikeAppModule::class])
@AnotherBikeAppScope
interface AnotherBikeAppComponent {

    fun providesContext(): Context
}
