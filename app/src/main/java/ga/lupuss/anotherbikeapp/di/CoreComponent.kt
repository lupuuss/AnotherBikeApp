package ga.lupuss.anotherbikeapp.di

import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import dagger.Component
import ga.lupuss.anotherbikeapp.models.memory.FileObserverFactory
import ga.lupuss.anotherbikeapp.models.memory.FilesManager
import java.util.*
import javax.inject.Scope

@Scope
annotation class CoreScope

@CoreScope
@Component(modules = [CoreModule::class, MemoryModule::class, FirebaseModule::class])
interface CoreComponent {

    fun providesContext(): Context
    fun providesLocale(): Locale
    fun providesTimeProvider(): () -> Long
    fun providesSharedPreferences(): SharedPreferences
    fun providesFilesManager(): FilesManager
    fun providesFileObserverFactory(): FileObserverFactory
    fun providesGson(): Gson
    fun providesFirebaseAuth(): FirebaseAuth
    fun providesFirebaseFireStore(): FirebaseFirestore
}