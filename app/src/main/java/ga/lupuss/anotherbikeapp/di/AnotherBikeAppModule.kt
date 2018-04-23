package ga.lupuss.anotherbikeapp.di

import android.content.Context
import android.support.v4.os.ConfigurationCompat
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
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
}