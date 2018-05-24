package ga.lupuss.anotherbikeapp.di

import android.content.Context
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import ga.lupuss.anotherbikeapp.models.memory.FileObserverFactory
import ga.lupuss.anotherbikeapp.models.memory.FilesManager

@Module(includes = [BasicModule::class])
class MemoryModule {

    @Provides
    @CoreScope
    fun getFilesManager(gson: Gson) = FilesManager(gson)

    @Provides
    @CoreScope
    fun fileObserverFactory(): FileObserverFactory = FileObserverFactory()
}