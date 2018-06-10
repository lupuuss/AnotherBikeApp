package ga.lupuss.anotherbikeapp.di

import com.google.gson.Gson
import dagger.Module
import dagger.Provides

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
}