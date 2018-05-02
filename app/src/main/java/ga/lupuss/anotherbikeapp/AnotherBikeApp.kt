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
        initDefaultUser()

        coreComponent = DaggerCoreComponent
                .builder()
                .basicModule(BasicModule(this.applicationContext))
                .build()

        if (mainComponent == null) {

            val lastUser = coreComponent.providesSharedPreferences().getString(Prefs.LAST_USER, "")

            if (lastUser != "") {

                val user = coreComponent.providesGson().fromJson(lastUser, User::class.java)
                initMainComponentWithUser(
                        User.newInstance(coreComponent.providesPathsGenerator(), user.name, user.isDefault)
                )
            }
        }
    }

    fun initMainComponentWithUser(user: User) {

        coreComponent.providesSharedPreferences().edit().putString(
                Prefs.LAST_USER,
                coreComponent.providesGson().toJson(user)
        ).apply()

        mainComponent = DaggerAnotherBikeAppComponent
                .builder()
                .userModule(UserModule(user))
                .coreComponent(coreComponent)
                .build()
    }

    private fun initDefaultUser() {

        val routesPath = File(filesDir, DEFAULT_PROFILE_ROUTES_PATH)
        User.defaultUser = User(
                DEFAULT_PROFILE_NAME,
                true,
                routesPath,
                routesPath,
                FilesManager(Gson()).makeChildrenListFor(routesPath)
        )
    }
}