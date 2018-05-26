package ga.lupuss.anotherbikeapp.models.routes

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import ga.lupuss.anotherbikeapp.models.User
import ga.lupuss.anotherbikeapp.models.firebase.pojo.FirebaseShortRouteData
import ga.lupuss.anotherbikeapp.models.pojo.ExtendedRouteData
import ga.lupuss.anotherbikeapp.models.pojo.ShortRouteData
import timber.log.Timber
import java.util.*


class RoutesManager(val user: User,
                    val routeKeeper: TempRouteKeeper,
                    firebaseFirestore: FirebaseFirestore,
                    val locale: Locale) {

    private val dataChangedListeners = mutableListOf<OnRoutesChangedListener>()
    private var data: List<FirebaseShortRouteData> = listOf()
    private var routesCount = -1
    private val userPath = "$FIREB_USERS/${user.firebaseUser!!.uid}"
    private val routesPath = "$userPath/$FIREB_ROUTES"
    private val routesQuery = firebaseFirestore
            .collection(routesPath)
            .orderBy(FIREB_ROUTES_START_TIME)

    private var lastDocumentSnapshot: DocumentSnapshot? = null

    init {

        user.firebaseUser ?: throw IllegalStateException("No auth")
    }

    fun addOnRoutesChangedListener(onRoutesChangedListener: OnRoutesChangedListener) {
        dataChangedListeners.add(onRoutesChangedListener)
    }

    fun removeOnRoutesChangedListener(onRoutesChangedListener: OnRoutesChangedListener) {
        dataChangedListeners.remove(onRoutesChangedListener)
    }

    @Suppress("UNCHECKED_CAST")
    fun readQuickRoute(position: Int): ShortRouteData {

        return data[position]
    }

    fun readRoute(id: String): ExtendedRouteData {

        return ExtendedRouteData(
                "",
                0.0,
                0.0,
                0.0,
                1000L,
                "w232",
                1000L,
                mutableListOf()
        )
    }

    fun routesCount(): Int = data.size

    fun saveRoute(routeData: ExtendedRouteData) {
        TODO()
    }

    fun requestMoreData(onDataEnd: (() -> Unit)?, onFail: ((Exception) -> Unit)?) {

        if ((routesCount == 0 || routesCount <= data.size) && routesCount != -1) {

            onDataEnd?.invoke()

        } else if (lastDocumentSnapshot == null) {
            routesQuery
                    .limit(DEFAULT_LIMIT)
                    .get()
                    .addOnSuccessListener {
                        fetchQuery(it)
                    }
                    .addOnFailureListener {
                        onFail?.invoke(it)
                    }
        } else {

            lastDocumentSnapshot?.let {

                routesQuery
                        .startAfter(it)
                        .limit(DEFAULT_LIMIT + data.size)
                        .get()
                        .addOnSuccessListener {
                            fetchQuery(it)
                        }
                        .addOnFailureListener {
                            onFail?.invoke(it)
                        }

            }
        }

    }

    private fun fetchQuery(snapshot: QuerySnapshot) {

        val mutList = mutableListOf<FirebaseShortRouteData>()

        if (!snapshot.documents.isEmpty()) {

            for (docSnap in snapshot.documents) {

                Timber.d(docSnap.data.toString())
                mutList.add(
                        docSnap.toObject(FirebaseShortRouteData::class.java)
                                .also { it.id = docSnap.id }
                )
            }

            lastDocumentSnapshot = snapshot.last()
            data = mutList.toList()

            dataChangedListeners.forEach { it.onRoutesChanged() }
        }
    }

    companion object {
        // Firebase path consts starts with FIREB
        //      if its a field
        //      it must FIREB_{COLLECTION}_{FIELD}
        const val FIREB_USERS = "users"
        const val FIREB_ROUTES = "routes"
        const val FIREB_ROUTES_START_TIME = "startTime"
        const val DEFAULT_LIMIT = 10L
    }
}