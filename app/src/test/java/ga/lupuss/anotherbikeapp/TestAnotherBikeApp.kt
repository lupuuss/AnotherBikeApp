package ga.lupuss.anotherbikeapp

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import ga.lupuss.anotherbikeapp.di.AnotherBikeAppComponent
import ga.lupuss.anotherbikeapp.models.android.AndroidStringsResolver
import ga.lupuss.anotherbikeapp.models.android.AndroidTrackingServiceGovernor
import ga.lupuss.anotherbikeapp.models.base.*
import ga.lupuss.anotherbikeapp.models.dataclass.ShortRouteData
import ga.lupuss.anotherbikeapp.models.dataclass.Statistic
import ga.lupuss.anotherbikeapp.models.trackingservice.TrackingService
import ga.lupuss.anotherbikeapp.ui.TrackingNotification
import ga.lupuss.anotherbikeapp.ui.modules.createaccount.CreateAccountComponent
import ga.lupuss.anotherbikeapp.ui.modules.createaccount.CreateAccountModule
import ga.lupuss.anotherbikeapp.ui.modules.createaccount.CreateAccountView
import ga.lupuss.anotherbikeapp.ui.modules.createaccount.DaggerCreateAccountComponent
import ga.lupuss.anotherbikeapp.ui.modules.login.DaggerLoginComponent
import ga.lupuss.anotherbikeapp.ui.modules.login.LoginComponent
import ga.lupuss.anotherbikeapp.ui.modules.login.LoginModule
import ga.lupuss.anotherbikeapp.ui.modules.login.LoginView
import ga.lupuss.anotherbikeapp.ui.modules.main.*
import ga.lupuss.anotherbikeapp.ui.modules.summary.DaggerSummaryComponent
import ga.lupuss.anotherbikeapp.ui.modules.summary.SummaryComponent
import ga.lupuss.anotherbikeapp.ui.modules.summary.SummaryModule
import ga.lupuss.anotherbikeapp.ui.modules.summary.SummaryView
import ga.lupuss.anotherbikeapp.ui.modules.tracking.DaggerTrackingComponent
import ga.lupuss.anotherbikeapp.ui.modules.tracking.TrackingComponent
import ga.lupuss.anotherbikeapp.ui.modules.tracking.TrackingModule
import ga.lupuss.anotherbikeapp.ui.modules.tracking.TrackingView

class TestAnotherBikeApp : AnotherBikeApp() {

    private val preferencesInteractor = mock<PreferencesInteractor> {
        on { appTheme }.then { AppTheme.GREY }
    }
    private val authInteractor: AuthInteractor = mock {  }
    private val trackingServiceGovernor: AndroidTrackingServiceGovernor = mock {
        on { serviceBinder }.then { mock<TrackingService.ServiceBinder>{} }
    }
    private lateinit var stringsResolver: StringsResolver
    private val routesManager: RoutesManager = mock { }
    private val trackingNotification: TrackingNotification = mock { }

    private val mockAnotherBikeAppComponent = mock<AnotherBikeAppComponent> {
        on { providesPreferencesInteractor() }.then { preferencesInteractor }
        on { providesStringResolver() }.then { stringsResolver }
        on { providesAuthInteractor() }.then { authInteractor }
        on { providesRoutesManager() }.then { routesManager }
        on { providesTrackingNotification() }.then { trackingNotification }
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

    override fun loginComponent(loginView: LoginView): LoginComponent {
        return DaggerLoginComponent.builder()
                .anotherBikeAppComponent(mockAnotherBikeAppComponent)
                .loginModule(LoginModule(loginView))
                .build()
    }

    override fun mainComponent(mainView: MainView): MainComponent {

        return DaggerMainComponent
                .builder()
                .anotherBikeAppComponent(mockAnotherBikeAppComponent)
                .mainModule(mock {
                    on { this.mainView }.then { mainView }
                    on { providesTrackingServiceGovernor(any(), any())}.then { trackingServiceGovernor }
                    on { providesMainPresenter(any(), any(), any(), any())}.then { mock<MainPresenter>{
                        on { onHistoryRecyclerItemCountRequest() }.then { 1 }
                        on { onHistoryRecyclerItemRequest(0) }.then {
                            ShortRouteData.Instance("", 0.0, 0.0, 0L, 0L)
                        }
                        on { speedUnit }.then { Statistic.Unit.KM_H }
                        on { distanceUnit }.then { Statistic.Unit.KM }
                    }}
                })
                .build()
    }

    override fun summaryComponent(summaryView: SummaryView): SummaryComponent {

        return DaggerSummaryComponent.builder()
                .anotherBikeAppComponent(mockAnotherBikeAppComponent)
                .summaryModule(SummaryModule(summaryView))
                .build()
    }
}