package ga.lupuss.anotherbikeapp.models.routes

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import ga.lupuss.anotherbikeapp.models.firebase.OnDocumentChanged
import ga.lupuss.anotherbikeapp.models.firebase.QueryLoadingManager
import ga.lupuss.anotherbikeapp.models.firebase.pojo.FirebasePoints
import ga.lupuss.anotherbikeapp.models.firebase.pojo.FirebaseRouteData
import ga.lupuss.anotherbikeapp.models.firebase.pojo.FirebaseShortRouteData
import ga.lupuss.anotherbikeapp.models.interfaces.RoutesManager
import ga.lupuss.anotherbikeapp.models.pojo.ExtendedRouteData
import ga.lupuss.anotherbikeapp.models.pojo.ShortRouteData
import java.util.*


class FirebaseRoutesManager(private val firebaseAuth: FirebaseAuth,
                            private val firebaseFirestore: FirebaseFirestore,
                            private val routeKeeper: TempRouteKeeper,
                            val locale: Locale) : RoutesManager {

    private val onRoutesChangedListeners = mutableListOf<OnDocumentChanged>()
    private val userPath = "$FIREB_USERS/${firebaseAuth.currentUser!!.uid}"
    private val routesPath = "$userPath/$FIREB_ROUTES"
    private val routesQuery = firebaseFirestore
            .collection(routesPath)
            .orderBy(FIREB_ROUTES_START_TIME, Query.Direction.DESCENDING)

    private val queryManager =
            QueryLoadingManager(routesQuery, DEFAULT_LIMIT, onRoutesChangedListeners)


    private fun DocumentSnapshot.toShortRouteData(): FirebaseShortRouteData =
            this.toObject(FirebaseShortRouteData::class.java).also { it.id = this.id }

    override fun addRoutesDataChangedListener(onRoutesChangedListener: OnDocumentChanged) {

        onRoutesChangedListeners.add(onRoutesChangedListener)
    }

    override fun removeOnRoutesDataChangedListener(onRoutesChangedListener: OnDocumentChanged) {

        onRoutesChangedListeners.remove(onRoutesChangedListener)
    }

    override fun requestMoreShortRouteData(onDataEnd: (() -> Unit)?, onFail: ((Exception) -> Unit)?) {

        queryManager.loadMoreDocuments(onDataEnd, onFail)
    }

    override fun readShortRouteData(position: Int): ShortRouteData {

        return queryManager.readDocument(position).toShortRouteData()
    }

    override fun shortRouteDataCount() = queryManager.size

    override fun requestExtendedRoutesData(
            onDataOk: ((ExtendedRouteData) -> Unit)?,
            onDataFail: ((Exception) -> Unit)?) {

    }

    override fun saveRoute(routeData: ExtendedRouteData) {


        val newPointsRef = firebaseFirestore.collection("points").document()
        val newRouteRef = firebaseFirestore.collection(FIREB_ROUTES).document()
        val userRef = firebaseFirestore.collection(FIREB_USERS).document(firebaseAuth.currentUser!!.uid)

        firebaseFirestore
                .batch()
                .set(newPointsRef, FirebasePoints().apply {
                    this.points = routeData.points.map { GeoPoint(it.latitude, it.longitude) }
                })
                .set(newRouteRef, FirebaseRouteData().apply {
                    fillWith(routeData)
                    points = newPointsRef
                    user = userRef
                })
                .update(newPointsRef, "route", newRouteRef)
                .set(userRef.collection(FIREB_ROUTES).document(), FirebaseShortRouteData().apply {
                    fillWithShort(routeData)
                    more = newRouteRef
                })
                .commit()
    }

    override fun keepTempRoute(routeData: ExtendedRouteData) {

        routeKeeper.keep(routeData)
    }

    override fun getTempRoute(): ExtendedRouteData? = routeKeeper.getRoute()

    override fun clearTempRoute() {

        routeKeeper.clear()
    }

    companion object {
        // Firebase path consts starts with FIREB
        //      if its a field
        //      it must match FIREB_{COLLECTION}_{FIELD}
        const val FIREB_USERS = "users"
        const val FIREB_ROUTES = "routes"
        const val FIREB_ROUTES_START_TIME = "startTime"
        const val DEFAULT_LIMIT = 10L
    }
}