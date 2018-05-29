package ga.lupuss.anotherbikeapp.models

import android.content.Context
import com.google.firebase.auth.FirebaseUser
import ga.lupuss.anotherbikeapp.R

class User(
        var firebaseUser: FirebaseUser,
        context: Context
) {
    val name: String = if (firebaseUser.displayName != "")
        firebaseUser.displayName!!
    else if (firebaseUser.email != null && firebaseUser.email != "")
        firebaseUser.email!!
    else
        context.getString(R.string.user)
}