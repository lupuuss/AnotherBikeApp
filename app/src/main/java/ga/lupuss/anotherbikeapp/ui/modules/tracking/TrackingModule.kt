package ga.lupuss.anotherbikeapp.ui.modules.tracking

import dagger.Module
import dagger.Provides
import ga.lupuss.anotherbikeapp.models.trackingservice.TrackingService

@Module
class TrackingModule(view: TrackingView,
                     serviceBinder: TrackingService.ServiceBinder) {

    val trackingView = view
        @TrackingScope
        @Provides
        get

    val serviceBinder = serviceBinder
        @TrackingScope
        @Provides
        get
}