package ga.lupuss.anotherbikeapp

import android.app.Application
import android.content.Context
import com.jakewharton.threetenabp.AndroidThreeTen
import com.squareup.leakcanary.LeakCanary
import ga.lupuss.anotherbikeapp.di.*
import timber.log.Timber
import com.squareup.leakcanary.RefWatcher
import ga.lupuss.anotherbikeapp.models.base.TrackingServiceInteractor
import ga.lupuss.anotherbikeapp.ui.modules.tracking.DaggerTrackingComponent
import ga.lupuss.anotherbikeapp.ui.modules.tracking.TrackingComponent
import ga.lupuss.anotherbikeapp.ui.modules.tracking.TrackingModule
import ga.lupuss.anotherbikeapp.ui.modules.tracking.TrackingView


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
}