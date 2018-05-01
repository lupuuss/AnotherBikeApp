package ga.lupuss.anotherbikeapp.di

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
    fun getRoutesKeeper(filesManager: FilesManager, user: User) =
            RoutesManager(filesManager, user)
}