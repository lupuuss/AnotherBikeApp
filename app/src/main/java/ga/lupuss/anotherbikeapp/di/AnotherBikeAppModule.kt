package ga.lupuss.anotherbikeapp.di

import android.content.Context
import android.os.FileObserver
import dagger.Module
import dagger.Provides
import ga.lupuss.anotherbikeapp.models.FileObserverFactory
import ga.lupuss.anotherbikeapp.models.FilesManager
import ga.lupuss.anotherbikeapp.models.RoutesManager
import ga.lupuss.anotherbikeapp.models.pojo.User

@Module()
class AnotherBikeAppModule {

    @Provides
    @AnotherBikeAppScope
    fun getRoutesKeeper(filesManager: FilesManager,
                        user: User,
                        context: Context,
                        fileObserverFactory: FileObserverFactory) =

            RoutesManager(filesManager, user, fileObserverFactory, context)

}
