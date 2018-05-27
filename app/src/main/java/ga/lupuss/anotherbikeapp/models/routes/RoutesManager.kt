package ga.lupuss.anotherbikeapp.models.routes

import com.google.firebase.firestore.*
import ga.lupuss.anotherbikeapp.models.User
import ga.lupuss.anotherbikeapp.models.firebase.pojo.FirebaseShortRouteData
import ga.lupuss.anotherbikeapp.models.pojo.ExtendedRouteData
import ga.lupuss.anotherbikeapp.models.pojo.ShortRouteData
import java.util.*


class RoutesManager(val user: User,
                    val routeKeeper: TempRouteKeeper,
                    val firebaseFirestore: FirebaseFirestore,
                    val locale: Locale) {
    private val onDocumentsChangedListeners = mutableListOf<OnDocumentChanged>()
    private val userPath = "$FIREB_USERS/${user.firebaseUser!!.uid}"
    private val routesPath = "$userPath/$FIREB_ROUTES"
    private val routesQuery = firebaseFirestore
            .collection(routesPath)
            .orderBy(FIREB_ROUTES_START_TIME)

    private val routesQueryManager =
            QueryManager(routesQuery, DEFAULT_LIMIT, onDocumentsChangedListeners)

    init {

        var x = 1214333241343L
        for (g in 0..23)
            firebaseFirestore.collection(routesPath).document(g.toString()).set(
                    mapOf<String, Any>(
                            "name" to "Test #$g",
                            "avgSpeed" to 33,
                            "distance" to 1000L,
                            "duration" to 100000000L,
                            "more" to firebaseFirestore.document("routes/HxQctIkJ3z8DzIUbVNmj"),
                            "startTime" to x++
                    )
            )


        user.firebaseUser ?: throw IllegalStateException("No auth")
    }

    private fun DocumentSnapshot.toShortRouteData(): FirebaseShortRouteData =
            this.toObject(FirebaseShortRouteData::class.java).also { it.id = this.id }

    fun addOnQuickRoutesChangedListener(onRoutesChangedListener: OnDocumentChanged) {
        onDocumentsChangedListeners.add(onRoutesChangedListener)
    }

    fun removeOnQuickRoutesChangedListener(onRoutesChangedListener: OnDocumentChanged) {
        onDocumentsChangedListeners.remove(onRoutesChangedListener)
    }

    fun requestMoreQuickRoutes(onDataEnd: (() -> Unit)?, onFail: ((Exception) -> Unit)?) {
        routesQueryManager.loadMoreDocuments()
    }

    @Suppress("UNCHECKED_CAST")
    fun readQuickRoute(position: Int): ShortRouteData {

        return routesQueryManager.readDocument(position).toShortRouteData()
    }

    fun quickRoutesCount() = routesQueryManager.size

    fun requestExtendedRoutesData(
            onDataOk: ((ExtendedRouteData) -> Unit)?,
            onDataFail: ((Exception) -> Unit)?) {

    }

    fun saveRoute(routeData: ExtendedRouteData) {
        TODO()
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