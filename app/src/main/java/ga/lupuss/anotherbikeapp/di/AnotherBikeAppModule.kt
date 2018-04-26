package ga.lupuss.anotherbikeapp.di

import android.content.Context
import android.support.v4.os.ConfigurationCompat
import dagger.Module
import dagger.Provides

@Module(includes = [BasicModule::class])
class AnotherBikeAppModule {

    @Provides
    @AnotherBikeAppScope
    fun getLocale(context: Context) =
            ConfigurationCompat.getLocales(context.resources.configuration)[0]!!
}