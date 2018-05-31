package ga.lupuss.anotherbikeapp.di

import android.content.SharedPreferences
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import ga.lupuss.anotherbikeapp.models.android.AndroidPreferencesInteractor
import ga.lupuss.anotherbikeapp.models.routes.FirebaseRoutesManager
import ga.lupuss.anotherbikeapp.models.routes.TempRouteKeeper
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
                              locale: Locale): RoutesManager =
            FirebaseRoutesManager(firebaseAuth, firebaseFirestore, routesKeeper, locale)

    @Provides
    @AnotherBikeAppScope
    fun providesAuthInteractor(firebaseAuth: FirebaseAuth): AuthInteractor = FirebaseAuthInteractor(firebaseAuth)

    @Provides
    @AnotherBikeAppScope
    fun providesPreferencesInteractor(sharedPreferences: SharedPreferences): PreferencesInteractor =
            AndroidPreferencesInteractor(sharedPreferences)

}
