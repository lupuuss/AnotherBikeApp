package ga.lupuss.anotherbikeapp.di

import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides
import ga.lupuss.anotherbikeapp.models.User
import ga.lupuss.anotherbikeapp.models.routes.RoutesManager
import ga.lupuss.anotherbikeapp.models.routes.TempRouteKeeper
import java.util.*

@Module()
class AnotherBikeAppModule {

    @Provides
    @AnotherBikeAppScope
    fun providesSyncRoutesManager(user: User,
                                  routesKeeper: TempRouteKeeper,
                                  firebaseFirestore: FirebaseFirestore,
                                  locale: Locale): RoutesManager =
            RoutesManager(user, routesKeeper, firebaseFirestore, locale)


}
