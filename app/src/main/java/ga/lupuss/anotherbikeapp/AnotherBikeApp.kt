package ga.lupuss.anotherbikeapp

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen
import ga.lupuss.anotherbikeapp.di.*
import timber.log.Timber

class AnotherBikeApp : Application() {

    lateinit var anotherBikeAppComponent: AnotherBikeAppComponent

    companion object {

        fun get(application: Application): AnotherBikeApp {

            return application as AnotherBikeApp
        }
    }

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        AndroidThreeTen.init(this)

        anotherBikeAppComponent = DaggerAnotherBikeAppComponent
                .builder()
                .androidModule(AndroidModule(this.applicationContext))
                .build()
    }
}