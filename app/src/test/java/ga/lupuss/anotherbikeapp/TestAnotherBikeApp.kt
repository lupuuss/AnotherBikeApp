package ga.lupuss.anotherbikeapp

import android.content.Context
import com.nhaarman.mockito_kotlin.mock
import ga.lupuss.anotherbikeapp.models.android.AndroidStringsResolver
import ga.lupuss.anotherbikeapp.models.base.PreferencesInteractor
import ga.lupuss.anotherbikeapp.models.base.StringsResolver
import ga.lupuss.anotherbikeapp.models.base.TrackingServiceInteractor
import ga.lupuss.anotherbikeapp.ui.modules.tracking.DaggerTrackingComponent
import ga.lupuss.anotherbikeapp.ui.modules.tracking.TrackingComponent
import ga.lupuss.anotherbikeapp.ui.modules.tracking.TrackingModule
import ga.lupuss.anotherbikeapp.ui.modules.tracking.TrackingView

class TestAnotherBikeApp : AnotherBikeApp() {

    val preferencesInteractor = mock<PreferencesInteractor> {
        on { appTheme }.then { AppTheme.GREY }
    }

    lateinit var stringsResolver: StringsResolver

    override fun onCreate() {
        super.onCreate()
        stringsResolver = AndroidStringsResolver(this)
    }

    override fun isInUnitTests(): Boolean = true
    override fun trackingComponent(view: TrackingView, trackingServiceInteractor: TrackingServiceInteractor): TrackingComponent {
        return DaggerTrackingComponent.builder()
                .anotherBikeAppComponent(
                        mock {
                            on { providesPreferencesInteractor() }.then { preferencesInteractor }
                            on { providesStringResolver() }.then { stringsResolver }
                        }
                )
                .trackingModule(TrackingModule(view, trackingServiceInteractor))
                .build()
    }
}