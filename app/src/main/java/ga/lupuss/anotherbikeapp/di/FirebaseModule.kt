package ga.lupuss.anotherbikeapp.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.Module
import dagger.Provides

@Module
class FirebaseModule {

    @Provides
    @AnotherBikeAppScope
    fun firebaseAuth() = FirebaseAuth.getInstance().apply {
        this.useAppLanguage()
    }

    @Provides
    @AnotherBikeAppScope
    fun firebaseFirestore() = FirebaseFirestore.getInstance()
}