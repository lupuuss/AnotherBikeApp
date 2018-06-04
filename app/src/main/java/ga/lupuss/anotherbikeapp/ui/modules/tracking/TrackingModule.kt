package ga.lupuss.anotherbikeapp.ui.modules.tracking

import dagger.Module
import dagger.Provides
import ga.lupuss.anotherbikeapp.models.base.TrackingServiceInteractor

@Module
class TrackingModule(view: TrackingView,
                     serviceInteractor: TrackingServiceInteractor) {

    val trackingView = view
        @TrackingScope
        @Provides
        get

    val serviceInteractor = serviceInteractor
        @TrackingScope
        @Provides
        get
}