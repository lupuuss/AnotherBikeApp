package ga.lupuss.anotherbikeapp.ui.modules.tracking

import dagger.Module
import dagger.Provides
import ga.lupuss.anotherbikeapp.trackingservice.TrackingService

@Module
class TrackingModule(view: TrackingPresenter.IView,
                     serviceBinder: TrackingService.ServiceBinder) {

    val iView = view
        @TrackingScope
        @Provides
        get

    val serviceBinder = serviceBinder
        @Provides
        get
}