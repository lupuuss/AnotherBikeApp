package ga.lupuss.anotherbikeapp

import android.app.Application
import android.content.Context
import com.jakewharton.threetenabp.AndroidThreeTen
import com.squareup.leakcanary.LeakCanary
import ga.lupuss.anotherbikeapp.di.*
import timber.log.Timber
import com.squareup.leakcanary.RefWatcher
import ga.lupuss.anotherbikeapp.models.SignInVerifier
import ga.lupuss.anotherbikeapp.models.base.*
import ga.lupuss.anotherbikeapp.ui.modules.about.AboutAppComponent
import ga.lupuss.anotherbikeapp.ui.modules.about.AboutAppModule
import ga.lupuss.anotherbikeapp.ui.modules.about.AboutAppView
import ga.lupuss.anotherbikeapp.ui.modules.about.DaggerAboutAppComponent
import ga.lupuss.anotherbikeapp.ui.modules.createaccount.CreateAccountComponent
import ga.lupuss.anotherbikeapp.ui.modules.createaccount.CreateAccountModule
import ga.lupuss.anotherbikeapp.ui.modules.createaccount.CreateAccountView
import ga.lupuss.anotherbikeapp.ui.modules.createaccount.DaggerCreateAccountComponent
import ga.lupuss.anotherbikeapp.ui.modules.forgotpassword.DaggerForgotPasswordComponent
import ga.lupuss.anotherbikeapp.ui.modules.forgotpassword.ForgotPasswordComponent
import ga.lupuss.anotherbikeapp.ui.modules.forgotpassword.ForgotPasswordModule
import ga.lupuss.anotherbikeapp.ui.modules.forgotpassword.ForgotPasswordView
import ga.lupuss.anotherbikeapp.ui.modules.login.DaggerLoginComponent
import ga.lupuss.anotherbikeapp.ui.modules.login.LoginComponent
import ga.lupuss.anotherbikeapp.ui.modules.login.LoginModule
import ga.lupuss.anotherbikeapp.ui.modules.login.LoginView
import ga.lupuss.anotherbikeapp.ui.modules.main.DaggerMainComponent
import ga.lupuss.anotherbikeapp.ui.modules.main.MainComponent
import ga.lupuss.anotherbikeapp.ui.modules.main.MainModule
import ga.lupuss.anotherbikeapp.ui.modules.main.MainView
import ga.lupuss.anotherbikeapp.ui.modules.routephotos.DaggerRoutePhotosComponent
import ga.lupuss.anotherbikeapp.ui.modules.routephotos.RoutePhotosComponent
import ga.lupuss.anotherbikeapp.ui.modules.routephotos.RoutePhotosModule
import ga.lupuss.anotherbikeapp.ui.modules.routephotos.RoutePhotosView
import ga.lupuss.anotherbikeapp.ui.modules.routeshistory.DaggerRoutesHistoryComponent
import ga.lupuss.anotherbikeapp.ui.modules.routeshistory.RoutesHistoryComponent
import ga.lupuss.anotherbikeapp.ui.modules.routeshistory.RoutesHistoryModule
import ga.lupuss.anotherbikeapp.ui.modules.routeshistory.RoutesHistoryView
import ga.lupuss.anotherbikeapp.ui.modules.summary.DaggerSummaryComponent
import ga.lupuss.anotherbikeapp.ui.modules.summary.SummaryComponent
import ga.lupuss.anotherbikeapp.ui.modules.summary.SummaryModule
import ga.lupuss.anotherbikeapp.ui.modules.summary.SummaryView
import ga.lupuss.anotherbikeapp.ui.modules.tracking.DaggerTrackingComponent
import ga.lupuss.anotherbikeapp.ui.modules.tracking.TrackingComponent
import ga.lupuss.anotherbikeapp.ui.modules.tracking.TrackingModule
import ga.lupuss.anotherbikeapp.ui.modules.tracking.TrackingView
import ga.lupuss.anotherbikeapp.ui.modules.weather.DaggerWeatherComponent
import ga.lupuss.anotherbikeapp.ui.modules.weather.WeatherComponent
import ga.lupuss.anotherbikeapp.ui.modules.weather.WeatherModule
import ga.lupuss.anotherbikeapp.ui.modules.weather.WeatherView


open class AnotherBikeApp : Application() {

    lateinit var signInVerifier: SignInVerifier

    lateinit var anotherBikeAppComponent: AnotherBikeAppComponent

    var userComponent: UserComponent? = null

    companion object {

        fun get(application: Application): AnotherBikeApp {

            return application as AnotherBikeApp
        }

        fun getRefWatcher(context: Context): RefWatcher {
            val application = context.applicationContext as AnotherBikeApp
            return application.refWatcher
        }
    }

    open lateinit var refWatcher: RefWatcher

    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)

        // avoids sdk memory leak
        packageManager.getUserBadgedLabel("", android.os.Process.myUserHandle())
        Timber.plant(Timber.DebugTree())

        if (!isInUnitTests()) {

            if (LeakCanary.isInAnalyzerProcess(this)) {
                // This process is dedicated to LeakCanary for heap analysis.
                // You should not init your app in this process.
                return
            }

            refWatcher = LeakCanary.install(this)
        }

        anotherBikeAppComponent = DaggerAnotherBikeAppComponent
                .builder()
                .androidModule(AndroidModule(this.applicationContext))
                .build()

        anotherBikeAppComponent
                .providesTrackingNotification()
                .initNotificationChannelOreo(this)

        signInVerifier = SignInVerifier(anotherBikeAppComponent.providesAuthInteractor())
    }

    protected open fun isInUnitTests() = false

    fun initUserComponent() {
        userComponent = DaggerUserComponent.builder()
                .anotherBikeAppComponent(anotherBikeAppComponent)
                .build()
    }

    open fun trackingComponent(view: TrackingView, trackingServiceInteractor: TrackingServiceInteractor): TrackingComponent {

        return DaggerTrackingComponent
                .builder()
                .userComponent(userComponent!!)
                .trackingModule(TrackingModule(view, trackingServiceInteractor))
                .build()
    }

    open fun createAccountComponent(createAccountView: CreateAccountView): CreateAccountComponent {

        return DaggerCreateAccountComponent
                .builder()
                .anotherBikeAppComponent(anotherBikeAppComponent)
                .createAccountModule(CreateAccountModule(createAccountView))
                .build()
    }

    open fun loginComponent(loginView: LoginView): LoginComponent {

        return DaggerLoginComponent
                .builder()
                .anotherBikeAppComponent(anotherBikeAppComponent)
                .loginModule(LoginModule(loginView))
                .build()
    }

    open fun mainComponent(mainView: MainView): MainComponent {

        return DaggerMainComponent
                .builder()
                .userComponent(userComponent!!)
                .mainModule(MainModule(mainView))
                .build()
    }

    open fun summaryComponent(summaryView: SummaryView): SummaryComponent {

        return DaggerSummaryComponent
                .builder()
                .userComponent(userComponent!!)
                .summaryModule(SummaryModule(summaryView))
                .build()
    }

    open fun weatherComponent(weatherView: WeatherView): WeatherComponent {

        return DaggerWeatherComponent
                .builder()
                .userComponent(userComponent!!)
                .weatherModule(WeatherModule(weatherView))
                .build()
    }

    open fun routesHistoryComponent(routesHistoryView: RoutesHistoryView): RoutesHistoryComponent {
        return DaggerRoutesHistoryComponent
                .builder()
                .userComponent(userComponent!!)
                .routesHistoryModule(RoutesHistoryModule(routesHistoryView))
                .build()
    }

    open fun aboutAppComponent(aboutAppView: AboutAppView): AboutAppComponent {

        return DaggerAboutAppComponent
                .builder()
                .userComponent(userComponent!!)
                .aboutAppModule(AboutAppModule(aboutAppView))
                .build()
    }

    open fun forgotPasswordComponent(forgotPasswordView: ForgotPasswordView): ForgotPasswordComponent {

        return DaggerForgotPasswordComponent
                .builder()
                .anotherBikeAppComponent(anotherBikeAppComponent)
                .forgotPasswordModule(ForgotPasswordModule(forgotPasswordView))
                .build()
    }

    open fun routePhotosComponent(routePhotosView: RoutePhotosView): RoutePhotosComponent {

        return DaggerRoutePhotosComponent
                .builder()
                .userComponent(userComponent)
                .routePhotosModule(RoutePhotosModule(routePhotosView))
                .build()
    }
}