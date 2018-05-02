package ga.lupuss.anotherbikeapp.di

import android.content.Context
import com.google.gson.Gson
import dagger.Module
import dagger.Provides

@Module
class BasicModule(context: Context) {

    val context = context
        @Provides
        @CoreScope
        get

    val gson: Gson
        @Provides
        @CoreScope
        get() = Gson()
}