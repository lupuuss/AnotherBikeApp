package ga.lupuss.anotherbikeapp

import com.nhaarman.mockito_kotlin.mock
import ga.lupuss.anotherbikeapp.di.AnotherBikeAppComponent
import ga.lupuss.anotherbikeapp.models.android.AndroidStringsResolver
import ga.lupuss.anotherbikeapp.models.base.AuthInteractor
import ga.lupuss.anotherbikeapp.models.base.PreferencesInteractor
import ga.lupuss.anotherbikeapp.models.base.StringsResolver
import ga.lupuss.anotherbikeapp.models.base.TrackingServiceInteractor
import ga.lupuss.anotherbikeapp.ui.modules.createaccount.CreateAccountComponent
import ga.lupuss.anotherbikeapp.ui.modules.createaccount.CreateAccountModule
import ga.lupuss.anotherbikeapp.ui.modules.createaccount.CreateAccountView
import ga.lupuss.anotherbikeapp.ui.modules.createaccount.DaggerCreateAccountComponent
import ga.lupuss.anotherbikeapp.ui.modules.tracking.DaggerTrackingComponent
import ga.lupuss.anotherbikeapp.ui.modules.tracking.TrackingComponent
import ga.lupuss.anotherbikeapp.ui.modules.tracking.TrackingModule
import ga.lupuss.anotherbikeapp.ui.modules.tracking.TrackingView

class TestAnotherBikeApp : AnotherBikeApp() {

    private val preferencesInteractor = mock<PreferencesInteractor> {
        on { appTheme }.then { AppTheme.GREY }
    }
    private val authInteractor: AuthInteractor = mock {  }

    private lateinit var stringsResolver: StringsResolver

    private val mockAnotherBikeAppComponent = mock<AnotherBikeAppComponent> {
        on { providesPreferencesInteractor() }.then { preferencesInteractor }
        on { providesStringResolver() }.then { stringsResolver }
        on { providesAuthInteractor() }.then { authInteractor }
    }

    override fun onCreate() {
        super.onCreate()
        stringsResolver = AndroidStringsResolver(this)
    }

    override fun isInUnitTests(): Boolean = true
    override fun trackingComponent(view: TrackingView, trackingServiceInteractor: TrackingServiceInteractor): TrackingComponent {
        return DaggerTrackingComponent.builder()
                .anotherBikeAppComponent(mockAnotherBikeAppComponent)
                .trackingModule(TrackingModule(view, trackingServiceInteractor))
                .build()
    }

    override fun createAccountComponent(createAccountView: CreateAccountView): CreateAccountComponent {
        return DaggerCreateAccountComponent.builder()
                .anotherBikeAppComponent(mockAnotherBikeAppComponent)
                .createAccountModule(CreateAccountModule(createAccountView))
                .build()
    }
}