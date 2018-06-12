package ga.lupuss.anotherbikeapp

import android.app.Application
import android.content.Context
import android.os.Environment
import android.util.Log
import com.jakewharton.threetenabp.AndroidThreeTen
import com.squareup.leakcanary.LeakCanary
import ga.lupuss.anotherbikeapp.di.*
import timber.log.Timber
import com.squareup.leakcanary.RefWatcher
import ga.lupuss.anotherbikeapp.ui.extensions.checkPermission
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


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

        if (BuildConfig.DEBUG) {

            Timber.plant(Timber.DebugTree())
        } else if (applicationContext.checkPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            Timber.plant(FileLoggingTree(this, System.currentTimeMillis()))
        }

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

    inner class FileLoggingTree(context: Context, time: Long) : Timber.DebugTree() {

        private val mainLogsDir = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS),  "/AnotherBikeAppLogs")

        private val logFile = File(mainLogsDir, "log$time.html")

        init {
            if (!mainLogsDir.exists()) {
                mainLogsDir.mkdirs()
            }

            if (!logFile.exists()) {
                logFile.createNewFile()
            }
        }

        override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {

            if (priority == Log.VERBOSE) return

            val logTimeStamp = SimpleDateFormat("E MMM dd yyyy 'at' hh:mm:ss:SSS aaa", Locale.getDefault()).format(Date())

            try {

                logFile.printWriter().use {
                    it.println("<p style=\"background:lightgray;\"><strong style=\"background:lightblue;\">&nbsp&nbsp$logTimeStamp :&nbsp&nbsp</strong>&nbsp&nbsp$message</p>")
                }

            } catch (exception: Exception) {

                exception.printStackTrace()
                Timber.uproot(this)
            }

        }
    }
}