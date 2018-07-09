package ga.lupuss.anotherbikeapp.di

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import android.support.v4.os.ConfigurationCompat
import dagger.Module
import dagger.Provides
import ga.lupuss.anotherbikeapp.models.android.AndroidStringsResolver
import ga.lupuss.anotherbikeapp.models.base.StringsResolver
import ga.lupuss.anotherbikeapp.ui.TrackingNotification

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
    fun sharedPreferences(context: Context): SharedPreferences =
            PreferenceManager.getDefaultSharedPreferences(context)

    @Provides
    @AnotherBikeAppScope
    fun androidStringResolver(context: Context): StringsResolver = AndroidStringsResolver(context)

    @Provides
    @AnotherBikeAppScope
    fun trackingNotification() = TrackingNotification()
}