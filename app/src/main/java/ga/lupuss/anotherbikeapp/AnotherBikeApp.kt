package ga.lupuss.anotherbikeapp

import android.app.Application
import android.content.Context
import com.jakewharton.threetenabp.AndroidThreeTen
import com.squareup.leakcanary.LeakCanary
import ga.lupuss.anotherbikeapp.di.*
import timber.log.Timber
import com.squareup.leakcanary.RefWatcher



class AnotherBikeApp : Application() {

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
        Timber.plant(Timber.DebugTree())
        AndroidThreeTen.init(this)

        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return
        }

        refWatcher = LeakCanary.install(this)

        anotherBikeAppComponent = DaggerAnotherBikeAppComponent
                .builder()
                .androidModule(AndroidModule(this.applicationContext))
                .build()
    }
}