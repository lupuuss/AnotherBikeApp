package ga.lupuss.anotherbikeapp.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides

@Module
class FirebaseModule {

    @Provides
    @CoreScope
    fun firebaseAuth() = FirebaseAuth.getInstance()

    @Provides
    @CoreScope
    fun firebaseFirestore() = FirebaseFirestore.getInstance()

}