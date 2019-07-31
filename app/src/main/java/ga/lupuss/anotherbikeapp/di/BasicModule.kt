package ga.lupuss.anotherbikeapp.di

import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import ga.lupuss.anotherbikeapp.kotlin.SchedulersPackage
import ga.lupuss.anotherbikeapp.models.FilesWrapper
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

@Module
class BasicModule {

    val gson: Gson
        @Provides
        @AnotherBikeAppScope
        get() = Gson()

    val timeProvider: () -> Long = System::currentTimeMillis
        @Provides
        @AnotherBikeAppScope
        get

    @Provides
    @AnotherBikeAppScope
    fun providesSchedulersPackage(): SchedulersPackage =
            SchedulersPackage(Schedulers.io(), AndroidSchedulers.mainThread())

    @Provides
    @AnotherBikeAppScope
    fun providesFilesWrapper() = FilesWrapper()
}