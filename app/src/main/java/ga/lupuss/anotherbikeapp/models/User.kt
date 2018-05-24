package ga.lupuss.anotherbikeapp.models

import com.google.firebase.auth.FirebaseUser

class User(
        val name: String,
        var firebaseUser: FirebaseUser? = null,
        val isDefault: Boolean
)