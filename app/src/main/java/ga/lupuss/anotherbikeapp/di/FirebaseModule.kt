package ga.lupuss.anotherbikeapp.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides

@Module
class FirebaseModule {

    @Provides
    @AnotherBikeAppScope
    fun firebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance().apply {
        this.useAppLanguage()
    }

    @Provides
    @AnotherBikeAppScope
    fun firebaseFirestore(): FirebaseFirestore = FirebaseFirestore.getInstance().apply {
        this.firestoreSettings = FirebaseFirestoreSettings.Builder().setPersistenceEnabled(true).build()
    }

    @Provides
    @AnotherBikeAppScope
    fun firebaseStorage(): FirebaseStorage = FirebaseStorage.getInstance()
}