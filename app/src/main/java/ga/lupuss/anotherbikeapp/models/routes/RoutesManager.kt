package ga.lupuss.anotherbikeapp.models.routes

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import ga.lupuss.anotherbikeapp.models.User
import ga.lupuss.anotherbikeapp.models.firebase.pojo.FirebaseRouteData
import ga.lupuss.anotherbikeapp.models.pojo.ExtendedRouteData
import ga.lupuss.anotherbikeapp.models.pojo.RouteData
import timber.log.Timber
import java.lang.Exception
import java.util.*

const val FIREBASE_USERS = "users"
const val FIREBASE_ROUTES = "routes"

class RoutesManager(val user: User,
                    val routeKeeper: TempRouteKeeper,
                    val firebaseFirestore: FirebaseFirestore,
                    val locale: Locale) {

    private lateinit var routesList: List<String>

    init {
        user.firebaseUser ?: throw IllegalStateException("No auth")

        firebaseFirestore
                .collection(FIREBASE_USERS)
                .document(user.firebaseUser!!.uid)
                .addSnapshotListener { snapshot, exception ->

                    snapshot?.let {

                        routesList = fetchRoutes(it)
                    }

                    exception?.let {

                        Timber.d(exception)
                    }

                }
    }

    fun initialize(onComplete: (Boolean) -> Unit) {

        firebaseFirestore
                .collection(FIREBASE_USERS)
                .document(user.firebaseUser!!.uid)
                .get()
                .addOnSuccessListener {

                    if (it.exists()) {
                        routesList = fetchRoutes(it)
                        onComplete.invoke(true)
                    }
                }
                .addOnFailureListener {

                    onComplete.invoke(false)
                    Timber.d(it)
                }
    }

    fun isInitialized() = ::routesList.isInitialized

    fun saveRoute(routeData: ExtendedRouteData) {
        TODO()
    }

    @Suppress("UNCHECKED_CAST")
    fun readRoute(position: Int,
                  onReadOk: ((RouteData) -> Unit)?,
                  onReadFail: ((Exception) -> Unit)?) {

        firebaseFirestore
                .document(routesList[position])
                .get()
                .addOnSuccessListener {

                    onReadOk?.invoke(it.toObject(FirebaseRouteData::class.java).toRouteData(locale))

                }.addOnFailureListener {

                    onReadFail?.invoke(it)
                }
    }

    private fun fetchRoutes(snapshot: DocumentSnapshot): List<String> {

        return snapshot.get(FIREBASE_ROUTES).toString()
                .removePrefix("[")
                .removeSuffix("]")
                .split(",")
                .map { it.trim() }

}

    fun routesCount(): Int = if (isInitialized()) routesList.size else 0

}