package ga.lupuss.anotherbikeapp.di

import android.content.Context
import android.support.v4.os.ConfigurationCompat
import dagger.Module
import dagger.Provides
import ga.lupuss.anotherbikeapp.APP_PREFS
import ga.lupuss.anotherbikeapp.models.android.AndroidStringsResolver
import ga.lupuss.anotherbikeapp.models.interfaces.StringsResolver

@Module(includes = [BasicModule::class])
class AndroidModule(context: Context) {

    val context = context
        @Provides
        @AnotherBikeAppScope
        get

    @Provides
    @AnotherBikeAppScope
    fun locale(context: Context) =
            ConfigurationCompat.getLocales(context.resources.configuration)[0]!!


    @Provides
    @AnotherBikeAppScope
    fun sharedPreferences(context: Context) =
            context.getSharedPreferences(APP_PREFS, Context.MODE_PRIVATE)

    @Provides
    @AnotherBikeAppScope
    fun androidStringResolver(context: Context): StringsResolver = AndroidStringsResolver(context)
}