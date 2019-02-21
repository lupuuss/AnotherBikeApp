package ga.lupuss.anotherbikeapp.di

import android.content.Context
import android.support.v4.os.ConfigurationCompat
import com.squareup.picasso.Picasso
import dagger.Module
import dagger.Provides
import ga.lupuss.anotherbikeapp.models.android.AndroidPathsGenerator
import ga.lupuss.anotherbikeapp.models.android.AndroidResourceResolver
import ga.lupuss.anotherbikeapp.models.base.AuthInteractor
import ga.lupuss.anotherbikeapp.models.base.PathsGenerator
import ga.lupuss.anotherbikeapp.models.base.ResourceResolver
import ga.lupuss.anotherbikeapp.ui.TrackingNotification
import ga.lupuss.anotherbikeapp.ui.modules.routephotos.RoutePhotosScope

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

    @Provides
    @AnotherBikeAppScope
    fun providesPicasso(): Picasso = Picasso.get()

    @Provides
    @AnotherBikeAppScope
    fun providesPathsGenerator(
            authInteractor: AuthInteractor,
            timeProvider: () -> Long,
            context: Context
    ): PathsGenerator = AndroidPathsGenerator(context, timeProvider, authInteractor)
}