package ga.lupuss.anotherbikeapp

import android.app.Application
import ga.lupuss.anotherbikeapp.di.AnotherBikeAppComponent
import ga.lupuss.anotherbikeapp.di.BasicModule
import ga.lupuss.anotherbikeapp.di.DaggerAnotherBikeAppComponent
import ga.lupuss.anotherbikeapp.models.pojo.User
import timber.log.Timber

class AnotherBikeApp : Application() {

    lateinit var component: AnotherBikeAppComponent

    companion object {

        lateinit var currentUser: User

        fun get(application: Application): AnotherBikeApp {

            return application as AnotherBikeApp
        }
    }

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        component = DaggerAnotherBikeAppComponent
                .builder()
                .basicModule(BasicModule(this))
                .build()

        val routesKeeper = component
                .providesRoutesKeeper()

        currentUser = User(
                "default",
                routesKeeper.routesPathForUser("default").toString(),
                routesKeeper.routesFilesListForUser("default")
        )

    }


}