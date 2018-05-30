package ga.lupuss.anotherbikeapp.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import ga.lupuss.anotherbikeapp.models.routes.FirebaseRoutesManager
import ga.lupuss.anotherbikeapp.models.routes.TempRouteKeeper
import ga.lupuss.anotherbikeapp.models.firebase.FirebaseAuthInteractor
import java.util.*

@Module()
class AnotherBikeAppModule {

    @Provides
    @AnotherBikeAppScope
    fun providesRoutesManager(firebaseAuth: FirebaseAuth,
                              routesKeeper: TempRouteKeeper,
                              firebaseFirestore: FirebaseFirestore,
                              locale: Locale): FirebaseRoutesManager =
            FirebaseRoutesManager(firebaseAuth, firebaseFirestore, routesKeeper, locale)

    @Provides
    @AnotherBikeAppScope
    fun providesFirebaseLoginInteractor(firebaseAuth: FirebaseAuth) = FirebaseAuthInteractor(firebaseAuth)


}
