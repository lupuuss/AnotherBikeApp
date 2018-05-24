package ga.lupuss.anotherbikeapp

import android.app.Application
import com.jakewharton.threetenabp.AndroidThreeTen
import ga.lupuss.anotherbikeapp.di.*
import ga.lupuss.anotherbikeapp.models.User
import timber.log.Timber
class AnotherBikeApp : Application() {

    var mainComponent: AnotherBikeAppComponent? = null
    lateinit var coreComponent: CoreComponent

    companion object {

        fun get(application: Application): AnotherBikeApp {

            return application as AnotherBikeApp
        }
    }

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        AndroidThreeTen.init(this)

        coreComponent = DaggerCoreComponent
                .builder()
                .basicModule(BasicModule(this.applicationContext))
                .build()

    }

    fun initMainComponentWithUser(user: User) {

        mainComponent = DaggerAnotherBikeAppComponent
                .builder()
                .userModule(UserModule(user))
                .coreComponent(coreComponent)
                .build()
    }

}