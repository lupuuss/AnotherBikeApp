package ga.lupuss.anotherbikeapp

import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import com.squareup.leakcanary.RefWatcher
import ga.lupuss.anotherbikeapp.base.BaseActivity
import ga.lupuss.anotherbikeapp.base.BaseFragment
import ga.lupuss.anotherbikeapp.di.AnotherBikeAppComponent
import ga.lupuss.anotherbikeapp.di.UserComponent
import ga.lupuss.anotherbikeapp.models.android.AndroidResourceResolver
import ga.lupuss.anotherbikeapp.models.android.AndroidTrackingServiceGovernor
import ga.lupuss.anotherbikeapp.models.base.*
import ga.lupuss.anotherbikeapp.models.firebase.FirebaseRoutesManager
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
import ga.lupuss.anotherbikeapp.ui.modules.routephotos.DaggerRoutePhotosComponent
import ga.lupuss.anotherbikeapp.ui.modules.routephotos.RoutePhotosComponent
import ga.lupuss.anotherbikeapp.ui.modules.routephotos.RoutePhotosModule
import ga.lupuss.anotherbikeapp.ui.modules.routephotos.RoutePhotosView
import ga.lupuss.anotherbikeapp.ui.modules.routeshistory.DaggerRoutesHistoryComponent
import ga.lupuss.anotherbikeapp.ui.modules.routeshistory.RoutesHistoryComponent
import ga.lupuss.anotherbikeapp.ui.modules.routeshistory.RoutesHistoryModule
import ga.lupuss.anotherbikeapp.ui.modules.routeshistory.RoutesHistoryView
import ga.lupuss.anotherbikeapp.ui.modules.summary.*
import ga.lupuss.anotherbikeapp.ui.modules.tracking.DaggerTrackingComponent
import ga.lupuss.anotherbikeapp.ui.modules.tracking.TrackingComponent
import ga.lupuss.anotherbikeapp.ui.modules.tracking.TrackingModule
import ga.lupuss.anotherbikeapp.ui.modules.tracking.TrackingView
import ga.lupuss.anotherbikeapp.ui.modules.weather.DaggerWeatherComponent
import ga.lupuss.anotherbikeapp.ui.modules.weather.WeatherComponent
import ga.lupuss.anotherbikeapp.ui.modules.weather.WeatherModule
import ga.lupuss.anotherbikeapp.ui.modules.weather.WeatherView

class TestAnotherBikeApp : AnotherBikeApp() {

    private val preferencesInteractor = mock<PreferencesInteractor> {
        on { appTheme }.then { AppTheme.DARK }
    }
    private val authInteractor: AuthInteractor = mock {  }
    private val trackingServiceGovernor: AndroidTrackingServiceGovernor = mock {
        on { serviceBinder }.then { mock<TrackingService.ServiceBinder>{} }
    }
    private lateinit var resourceResolver: ResourceResolver
    private val routesManager: RoutesManager = mock <FirebaseRoutesManager> { }
    private val trackingNotification: TrackingNotification = mock { }

    private val mockUserComponent = mock<UserComponent> {
        on { providesPreferencesInteractor() }.then { preferencesInteractor }
        on { providesStringResolver() }.then { resourceResolver }
        on { providesAuthInteractor() }.then { authInteractor }
        on { providesRoutesManager() }.then { routesManager }
        on { providesTrackingNotification() }.then { trackingNotification }
        on { providesPathsGenerator() }.then { mock<PathsGenerator> { } }
        on { providesTimeProvider() }.then { { 0L } }
        on { providesWeatherManager() }.then { mock <WeatherManager> { } }

    }

    private val mockAnotherBikeAppComponent = mock <AnotherBikeAppComponent> {
        on { providesResourceResolver() }.then { resourceResolver }
        on { providesAuthInteractor() }.then { authInteractor }
        on { providesContext() }.then { this@TestAnotherBikeApp.applicationContext }
        on { providesFirebaseFirestore() }.then { mock { } }
        on { providesFirebaseStorage() }.then { mock { } }
        on { providesGson() }.then { mock { } }
        on { providesLocale() }.then { mock { } }
        on { providesSchedulers() }.then { mock { } }
        on { providesTimeProvider() }.then { mock { } }
        on { providesWeatherApi() }.then { mock { } }
    }

    override var refWatcher: RefWatcher
        get() = mock {}
        set(_) {}

    override fun onCreate() {
        super.onCreate()
        resourceResolver = AndroidResourceResolver(this)
        signInVerifier = mock {
            on { verifySignedIn(any<BaseActivity>()) }.then { true }
            on { verifySignedIn(any<BaseFragment>()) }.then { true }
        }
    }

    override fun isInUnitTests(): Boolean = true

    override fun trackingComponent(view: TrackingView, trackingServiceInteractor: TrackingServiceInteractor): TrackingComponent {

        return DaggerTrackingComponent.builder()
                .userComponent(mockUserComponent)
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
                .userComponent(mockUserComponent)
                .mainModule(mock {
                    on { this.mainView }.then { mainView }
                    on { providesTrackingServiceGovernor(any(), any())}.then { trackingServiceGovernor }
                    on { providesMainPresenter(any(), any(), any()) }.then { mock<MainPresenter> {}}
                })
                .build()
    }

    override fun summaryComponent(summaryView: SummaryView): SummaryComponent {

        return DaggerSummaryComponent.builder()
                .userComponent(mockUserComponent)
                .summaryModule(mock {
                    on { this.summaryView }.then { summaryView }
                    on { providesSummaryPresenter(any(), any(), any(), any()) }.then { mock<MainSummaryPresenter> {} }
                })
                .build()
    }

    override fun routePhotosComponent(routePhotosView: RoutePhotosView): RoutePhotosComponent {

        return DaggerRoutePhotosComponent.builder()
                .userComponent(mockUserComponent)
                .routePhotosModule(RoutePhotosModule(routePhotosView))
                .build()
    }

    override fun weatherComponent(weatherView: WeatherView): WeatherComponent {

        return DaggerWeatherComponent.builder()
                .userComponent(mockUserComponent)
                .weatherModule(WeatherModule(weatherView))
                .build()
    }

    override fun routesHistoryComponent(routesHistoryView: RoutesHistoryView): RoutesHistoryComponent {

        return DaggerRoutesHistoryComponent.builder()
                .userComponent(mockUserComponent)
                .routesHistoryModule(RoutesHistoryModule(routesHistoryView))
                .build()
    }
}