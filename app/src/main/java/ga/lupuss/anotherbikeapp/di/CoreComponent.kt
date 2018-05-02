package ga.lupuss.anotherbikeapp.di

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import dagger.Component
import ga.lupuss.anotherbikeapp.models.FileObserverFactory
import ga.lupuss.anotherbikeapp.models.FilesManager
import ga.lupuss.anotherbikeapp.models.PathsGenerator
import java.util.*
import javax.inject.Scope

@Scope
annotation class CoreScope

@CoreScope
@Component(modules = [CoreModule::class, MemoryModule::class])
interface CoreComponent {

    fun providesContext(): Context
    fun providesLocale(): Locale
    fun providesTimeProvider(): () -> Long
    fun providesSharedPreferences(): SharedPreferences
    fun providesFilesManager(): FilesManager
    fun providesFileObserverFactory(): FileObserverFactory
    fun providesPathsGenerator(): PathsGenerator
    fun providesGson(): Gson
}