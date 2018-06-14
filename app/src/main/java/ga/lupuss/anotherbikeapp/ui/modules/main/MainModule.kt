package ga.lupuss.anotherbikeapp.ui.modules.main

import dagger.Module
import dagger.Provides
import ga.lupuss.anotherbikeapp.models.android.AndroidTrackingServiceGovernor
import ga.lupuss.anotherbikeapp.models.base.StringsResolver
import ga.lupuss.anotherbikeapp.models.base.TrackingServiceGovernor

@Module
class MainModule(view: MainView) {
    val mainView = view
        @Provides
        @MainComponentScope
        get

    @Provides
    @MainComponentScope
    fun providesTrackingServiceGovernor(stringsResolver: StringsResolver): TrackingServiceGovernor =
            AndroidTrackingServiceGovernor(stringsResolver)
}