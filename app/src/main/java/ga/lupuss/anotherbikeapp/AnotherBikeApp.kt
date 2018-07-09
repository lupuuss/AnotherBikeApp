package ga.lupuss.anotherbikeapp

import android.app.Application
import android.content.Context
import com.jakewharton.threetenabp.AndroidThreeTen
import com.squareup.leakcanary.LeakCanary
import ga.lupuss.anotherbikeapp.di.*
import timber.log.Timber
import com.squareup.leakcanary.RefWatcher
import ga.lupuss.anotherbikeapp.models.base.TrackingServiceInteractor
import ga.lupuss.anotherbikeapp.ui.modules.createaccount.CreateAccountComponent
import ga.lupuss.anotherbikeapp.ui.modules.createaccount.CreateAccountModule
import ga.lupuss.anotherbikeapp.ui.modules.createaccount.CreateAccountView
import ga.lupuss.anotherbikeapp.ui.modules.createaccount.DaggerCreateAccountComponent
import ga.lupuss.anotherbikeapp.ui.modules.login.DaggerLoginComponent
import ga.lupuss.anotherbikeapp.ui.modules.login.LoginComponent
import ga.lupuss.anotherbikeapp.ui.modules.login.LoginModule
import ga.lupuss.anotherbikeapp.ui.modules.login.LoginView
import ga.lupuss.anotherbikeapp.ui.modules.main.DaggerMainComponent
import ga.lupuss.anotherbikeapp.ui.modules.main.MainComponent
import ga.lupuss.anotherbikeapp.ui.modules.main.MainModule
import ga.lupuss.anotherbikeapp.ui.modules.main.MainView
import ga.lupuss.anotherbikeapp.ui.modules.tracking.DaggerTrackingComponent
import ga.lupuss.anotherbikeapp.ui.modules.tracking.TrackingComponent
import ga.lupuss.anotherbikeapp.ui.modules.tracking.TrackingModule
import ga.lupuss.anotherbikeapp.ui.modules.tracking.TrackingView
import kotlin.math.log


open class AnotherBikeApp : Application() {

    lateinit var anotherBikeAppComponent: AnotherBikeAppComponent

    companion object {

        fun get(application: Application): AnotherBikeApp {

            return application as AnotherBikeApp
        }

        fun getRefWatcher(context: Context): RefWatcher {
            val application = context.applicationContext as AnotherBikeApp
            return application.refWatcher
        }
    }

    lateinit var refWatcher: RefWatcher

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
    }

    protected open fun isInUnitTests() = false

    open fun trackingComponent(view: TrackingView, trackingServiceInteractor: TrackingServiceInteractor): TrackingComponent {

        return DaggerTrackingComponent
                .builder()
                .anotherBikeAppComponent(this.anotherBikeAppComponent)
                .trackingModule(TrackingModule(view, trackingServiceInteractor))
                .build()
    }

    open fun createAccountComponent(createAccountView: CreateAccountView): CreateAccountComponent {

        return DaggerCreateAccountComponent
                .builder()
                .anotherBikeAppComponent(this.anotherBikeAppComponent)
                .createAccountModule(CreateAccountModule(createAccountView))
                .build()
    }

    open fun loginComponent(loginView: LoginView): LoginComponent {

        return DaggerLoginComponent
                .builder()
                .anotherBikeAppComponent(this.anotherBikeAppComponent)
                .loginModule(LoginModule(loginView))
                .build()
    }

    open fun mainComponent(mainView: MainView): MainComponent {

        return DaggerMainComponent
                .builder()
                .anotherBikeAppComponent(this.anotherBikeAppComponent)
                .mainModule(MainModule(mainView))
                .build()
    }
}