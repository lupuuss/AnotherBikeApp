package ga.lupuss.anotherbikeapp.di

import android.content.Context
import androidx.core.os.ConfigurationCompat
import dagger.Module
import dagger.Provides
import ga.lupuss.anotherbikeapp.models.android.AndroidResourceResolver
import ga.lupuss.anotherbikeapp.models.base.ResourceResolver
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
    fun androidStringResolver(context: Context): ResourceResolver = AndroidResourceResolver(context)

    @Provides
    @AnotherBikeAppScope
    fun trackingNotification() = TrackingNotification()

}