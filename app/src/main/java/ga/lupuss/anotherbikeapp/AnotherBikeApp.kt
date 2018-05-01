package ga.lupuss.anotherbikeapp

import android.app.Application
import com.google.gson.Gson
import com.jakewharton.threetenabp.AndroidThreeTen
import ga.lupuss.anotherbikeapp.di.*
import ga.lupuss.anotherbikeapp.models.FilesManager
import ga.lupuss.anotherbikeapp.models.pojo.User
import timber.log.Timber
import java.io.File

class AnotherBikeApp : Application() {

    var mainComponent: AnotherBikeAppComponent? = null

    companion object {

        fun get(application: Application): AnotherBikeApp {

            return application as AnotherBikeApp
        }
    }

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
        AndroidThreeTen.init(this)

        initDefaultUser()

        // just for now
        // firebase not implemented yet
        mainComponent = DaggerAnotherBikeAppComponent
                .builder()
                .basicModule(BasicModule(this))
                .userModule(UserModule(User.defaultUser))
                .build()

    }

    private fun initDefaultUser() {

        val routesPath = File(filesDir, DEFAULT_PROFILE_ROUTES_PATH)
        User.defaultUser = User(
                DEFAULT_PROFILE_NAME,
                routesPath,
                routesPath,
                FilesManager(Gson()).makeChildrenListFor(routesPath),
                true
        )
    }
}