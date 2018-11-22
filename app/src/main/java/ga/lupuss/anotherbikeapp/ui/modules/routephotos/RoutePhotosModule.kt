package ga.lupuss.anotherbikeapp.ui.modules.routephotos

import android.content.Context
import dagger.Module
import dagger.Provides
import ga.lupuss.anotherbikeapp.models.android.AndroidPathsGenerator
import ga.lupuss.anotherbikeapp.models.base.AuthInteractor
import ga.lupuss.anotherbikeapp.models.base.PathsGenerator

@Module
class RoutePhotosModule(view: RoutePhotosView) {

    val view: RoutePhotosView = view
        @Provides
        @RoutePhotosScope
        get

    @Provides
    @RoutePhotosScope
    fun providesPathsGenerator(
            authInteractor: AuthInteractor,
            timeProvider: () -> Long,
            context: Context
    ): PathsGenerator = AndroidPathsGenerator(context, timeProvider, authInteractor)
}