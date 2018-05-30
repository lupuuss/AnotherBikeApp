package ga.lupuss.anotherbikeapp.di

import android.content.Context
import com.google.gson.Gson
import dagger.Module
import dagger.Provides

@Module
class BasicModule(context: Context) {

    val context = context
        @Provides
        @AnotherBikeAppScope
        get

    val gson: Gson
        @Provides
        @AnotherBikeAppScope
        get() = Gson()
}