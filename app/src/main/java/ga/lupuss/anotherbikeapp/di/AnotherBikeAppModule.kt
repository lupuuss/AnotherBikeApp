package ga.lupuss.anotherbikeapp.di

import android.content.Context
import dagger.Module
import dagger.Provides

@Module
class AnotherBikeAppModule(context: Context) {

    val context: Context = context
        @Provides
        @AnotherBikeAppScope
        get
}