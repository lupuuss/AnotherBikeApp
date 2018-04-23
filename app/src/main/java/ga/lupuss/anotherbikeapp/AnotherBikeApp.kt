package ga.lupuss.anotherbikeapp

import android.app.Application
import ga.lupuss.anotherbikeapp.di.AnotherBikeAppComponent
import ga.lupuss.anotherbikeapp.di.AnotherBikeAppModule
import ga.lupuss.anotherbikeapp.di.DaggerAnotherBikeAppComponent
import timber.log.Timber

class AnotherBikeApp : Application() {

    lateinit var component: AnotherBikeAppComponent

    companion object {

        fun get(application: Application): AnotherBikeApp {

            return application as AnotherBikeApp
        }
    }

    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())

        component = DaggerAnotherBikeAppComponent
                .builder()
                .anotherBikeAppModule(AnotherBikeAppModule(applicationContext))
                .build()
    }
}