package ga.lupuss.anotherbikeapp.di

import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import ga.lupuss.anotherbikeapp.models.android.AndroidPreferencesInteractor
import ga.lupuss.anotherbikeapp.models.firebase.FirebaseRoutesManager
import ga.lupuss.anotherbikeapp.models.firebase.TempRouteKeeper
import ga.lupuss.anotherbikeapp.models.firebase.FirebaseAuthInteractor
import ga.lupuss.anotherbikeapp.models.interfaces.AuthInteractor
import ga.lupuss.anotherbikeapp.models.interfaces.PreferencesInteractor
import ga.lupuss.anotherbikeapp.models.interfaces.RoutesManager
import java.util.*

@Module()
class AnotherBikeAppModule {

    @Provides
    @AnotherBikeAppScope
    fun providesRoutesManager(firebaseAuth: FirebaseAuth,
                              routesKeeper: TempRouteKeeper,
                              firebaseFirestore: FirebaseFirestore,
                              locale: Locale,
                              gson: Gson): RoutesManager =
            FirebaseRoutesManager(firebaseAuth, firebaseFirestore, routesKeeper, locale, gson)

    @Provides
    @AnotherBikeAppScope
    fun providesAuthInteractor(firebaseAuth: FirebaseAuth): AuthInteractor = FirebaseAuthInteractor(firebaseAuth)

    @Provides
    @AnotherBikeAppScope
    fun providesPreferencesInteractor(sharedPreferences: SharedPreferences, context: Context): PreferencesInteractor =
            AndroidPreferencesInteractor(sharedPreferences, context)

}
