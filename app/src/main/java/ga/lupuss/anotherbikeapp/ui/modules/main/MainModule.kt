package ga.lupuss.anotherbikeapp.ui.modules.main

import dagger.Module
import dagger.Provides
import ga.lupuss.anotherbikeapp.models.base.TrackingServiceGovernor

@Module
class MainModule(view: MainView, trackingServiceGovernor: TrackingServiceGovernor) {
    val mainView = view
        @Provides
        @MainComponentScope
        get

    val trackingServiceGovernor = trackingServiceGovernor
        @Provides
        @MainComponentScope
        get
}