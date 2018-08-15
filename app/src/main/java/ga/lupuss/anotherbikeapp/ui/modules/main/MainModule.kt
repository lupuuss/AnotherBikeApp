package ga.lupuss.anotherbikeapp.ui.modules.main

import dagger.Module
import dagger.Provides
import ga.lupuss.anotherbikeapp.models.android.AndroidTrackingServiceGovernor
import ga.lupuss.anotherbikeapp.models.base.*
import ga.lupuss.anotherbikeapp.ui.TrackingNotification

@Module
class MainModule(view: MainView) {
    val mainView = view
        @Provides
        @MainComponentScope
        get

    @Provides
    @MainComponentScope
    fun providesTrackingServiceGovernor(
            stringsResolver: StringsResolver,
            trackingNotification: TrackingNotification

    ): TrackingServiceGovernor = AndroidTrackingServiceGovernor(stringsResolver, trackingNotification)

    @Provides
    @MainComponentScope
    fun providesMainPresenter(routesManager: RoutesManager,
                              authInteractor: AuthInteractor,
                              trackingServiceGovernor: TrackingServiceGovernor): MainPresenter =
            MainPresenter(routesManager, authInteractor, trackingServiceGovernor, mainView)
}