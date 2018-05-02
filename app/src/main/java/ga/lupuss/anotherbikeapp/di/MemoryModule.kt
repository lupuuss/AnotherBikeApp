package ga.lupuss.anotherbikeapp.di

import android.content.Context
import android.os.FileObserver
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import ga.lupuss.anotherbikeapp.models.FileObserverFactory
import ga.lupuss.anotherbikeapp.models.FilesManager
import ga.lupuss.anotherbikeapp.models.PathsGenerator

@Module(includes = [BasicModule::class])
class MemoryModule {

    @Provides
    @CoreScope
    fun getFilesManager(gson: Gson) = FilesManager(gson)

    @Provides
    @CoreScope
    fun fileObserverFactory(): FileObserverFactory = FileObserverFactory()

    @Provides
    @CoreScope
    fun pathsGenerator(context: Context): PathsGenerator = PathsGenerator(context)
}