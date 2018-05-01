package ga.lupuss.anotherbikeapp.di

import android.content.Context
import android.os.FileObserver
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import ga.lupuss.anotherbikeapp.models.FilesManager
import ga.lupuss.anotherbikeapp.models.RoutesManager
import ga.lupuss.anotherbikeapp.models.pojo.User

@Module(includes = [BasicModule::class])
class MemoryModule {

    @Provides
    @AnotherBikeAppScope
    fun getFilesManager(gson: Gson) = FilesManager(gson)


    @Provides
    @AnotherBikeAppScope
    fun getRoutesKeeper(filesManager: FilesManager,
                        user: User,
                        context: Context) =
            RoutesManager(filesManager, user, fileObserverFactory, context)

    val fileObserverFactory: (String, Int, (Int, String?) -> Unit) -> FileObserver =

            { path: String, mask: Int, onEvent: (Int, String?) -> Unit ->
                object : FileObserver(path, mask) {
                    override fun onEvent(p0: Int, p1: String?) {
                        onEvent.invoke(p0, p1)
                    }

                }
            }
        @Provides
        @AnotherBikeAppScope
        get
}