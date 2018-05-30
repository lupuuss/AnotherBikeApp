package ga.lupuss.anotherbikeapp.di

import android.content.Context
import android.support.v4.os.ConfigurationCompat
import dagger.Module
import dagger.Provides
import ga.lupuss.anotherbikeapp.Prefs

@Module(includes = [BasicModule::class])
class CoreModule {

    @Provides
    @AnotherBikeAppScope
    fun locale(context: Context) =
            ConfigurationCompat.getLocales(context.resources.configuration)[0]!!


    @Provides
    @AnotherBikeAppScope
    fun sharedPreferences(context: Context) =
            context.getSharedPreferences(Prefs.APP_PREFS, Context.MODE_PRIVATE)


    val timeProvider: () -> Long = System::currentTimeMillis
        @Provides
        @AnotherBikeAppScope
        get
}