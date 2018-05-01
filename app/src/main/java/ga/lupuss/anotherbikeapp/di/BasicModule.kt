package ga.lupuss.anotherbikeapp.di

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import ga.lupuss.anotherbikeapp.APP_PREFS

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