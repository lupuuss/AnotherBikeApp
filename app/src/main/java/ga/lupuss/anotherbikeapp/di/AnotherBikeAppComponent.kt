package ga.lupuss.anotherbikeapp.di

import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import dagger.Component
import ga.lupuss.anotherbikeapp.models.android.AndroidStringsResolver
import ga.lupuss.anotherbikeapp.models.interfaces.AuthInteractor
import ga.lupuss.anotherbikeapp.models.interfaces.PreferencesInteractor
import ga.lupuss.anotherbikeapp.models.interfaces.RoutesManager
import ga.lupuss.anotherbikeapp.models.interfaces.StringsResolver
import ga.lupuss.anotherbikeapp.models.routes.FirebaseRoutesManager
import ga.lupuss.anotherbikeapp.ui.StartActivity
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

    fun providesRoutesManager(): RoutesManager
    fun providesStringResolver(): StringsResolver
    fun providesAuthInteractor(): AuthInteractor
    fun providesPreferencesInteractor(): PreferencesInteractor
}
