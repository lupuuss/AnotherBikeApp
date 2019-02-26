package ga.lupuss.anotherbikeapp.ui.modules.routephotos

import dagger.Module
import dagger.Provides

@Module
class RoutePhotosModule(view: RoutePhotosView) {

    val view: RoutePhotosView = view
        @Provides
        @RoutePhotosScope
        get
}