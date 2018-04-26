package ga.lupuss.anotherbikeapp.di

import android.content.Context
import android.support.v4.os.ConfigurationCompat
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import ga.lupuss.anotherbikeapp.models.FilesManager
import java.io.File
import java.util.*

@Module
class AnotherBikeAppModule(context: Context) {

    val context: Context = context
        @Provides
        @AnotherBikeAppScope
        get

    val locale: Locale
        @Provides
        @AnotherBikeAppScope
        get() = ConfigurationCompat.getLocales(context.resources.configuration)[0]


    val gson: Gson
        @Provides
        @AnotherBikeAppScope
        get() = Gson()

    val filesManager: FilesManager
        @Provides
        @AnotherBikeAppScope
        get() = FilesManager(gson)
}