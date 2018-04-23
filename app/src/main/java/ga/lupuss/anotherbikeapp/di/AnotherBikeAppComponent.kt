package ga.lupuss.anotherbikeapp.di

import android.content.Context
import com.google.gson.Gson
import dagger.Component
import java.util.*
import javax.inject.Scope

@Scope
annotation class AnotherBikeAppScope

@Component(modules = [AnotherBikeAppModule::class])
@AnotherBikeAppScope
interface AnotherBikeAppComponent {

    fun providesContext(): Context
    fun providesGson(): Gson
    fun providesLocale(): Locale
}
