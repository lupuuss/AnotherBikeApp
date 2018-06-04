package ga.lupuss.anotherbikeapp.models.firebase


import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.google.gson.Gson
import ga.lupuss.anotherbikeapp.models.firebase.pojo.FirebasePoints
import ga.lupuss.anotherbikeapp.models.firebase.pojo.FirebaseRouteData
import ga.lupuss.anotherbikeapp.models.firebase.pojo.FirebaseShortRouteData
import ga.lupuss.anotherbikeapp.models.base.RoutesManager
import ga.lupuss.anotherbikeapp.models.dataclass.ExtendedRouteData
import ga.lupuss.anotherbikeapp.models.dataclass.RouteData
import ga.lupuss.anotherbikeapp.models.dataclass.ShortRouteData
import ga.lupuss.anotherbikeapp.models.base.RouteReference
import ga.lupuss.anotherbikeapp.models.base.RouteReferenceSerializer
import timber.log.Timber
import java.lang.IllegalArgumentException
import java.util.*


class FirebaseRoutesManager(private val firebaseAuth: FirebaseAuth,
                            private val firebaseFirestore: FirebaseFirestore,
                            private val routeKeeper: TempRouteKeeper,
                            private val locale: Locale,
                            gson: Gson): RoutesManager {

    private val onRoutesChangedListeners = mutableListOf<OnDocumentChanged>()
    private val userPath = "$FIREB_USERS/${firebaseAuth.currentUser!!.uid}"
    private val routesPath = "$userPath/$FIREB_ROUTES"
    private val routesQuery = firebaseFirestore
            .collection(routesPath)
            .orderBy(FIREB_ROUTES_START_TIME, Query.Direction.DESCENDING)

    private val queryManager =
            QueryLoadingManager(routesQuery, DEFAULT_LIMIT, onRoutesChangedListeners)


    private fun DocumentSnapshot.toFirebaseShortRouteData(): FirebaseShortRouteData =
            this.toObject(FirebaseShortRouteData::class.java)!!

    private fun DocumentSnapshot.toFirebaseRouteData(): FirebaseRouteData =
            this.toObject(FirebaseRouteData::class.java)!!

    private fun DocumentSnapshot.toFirebasePoints(): FirebasePoints =
            this.toObject(FirebasePoints::class.java)!!

    override val routeReferenceSerializer: RouteReferenceSerializer = FirebaseRouteReferenceSerializer(gson, firebaseFirestore)

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

        return queryManager.readDocument(position).toFirebaseShortRouteData()
    }

    override fun shortRouteDataCount() = queryManager.size

    override fun getRouteReference(position: Int): RouteReference {

        val shortRouteDataSnap = queryManager.readDocument(position)
        val shortRouteData = shortRouteDataSnap.toFirebaseShortRouteData()

        return FirebaseRouteReference(shortRouteDataSnap.reference, shortRouteData.route, shortRouteData.points)
    }

    override fun requestExtendedRoutesData(
            routeReference: RouteReference,
            onDataOk: ((ExtendedRouteData) -> Unit)?,
            onDataFail: ((Exception) -> Unit)?) {

        if (routeReference !is FirebaseRouteReference) {
            throw IllegalArgumentException(WRONG_REFERENCE_MESSAGE)
        }

        if (routeReference.routeReference == null) {
            onDataFail?.invoke(Exception(""))
            return
        }

        if (routeReference.pointsReference == null) {
            Timber.d("Points reference is null!")
        }

        var routeData: RouteData? = null

        routeReference.routeReference!!.get()
                .continueWithTask {

                    if (it.result.exists()) {

                        routeData = it.result.toFirebaseRouteData().toRouteData(locale)
                    }

                    routeReference.pointsReference?.get()
                }.addOnSuccessListener {

                    onDataOk?.invoke(routeData!!.toExtendedRouteData( it.toFirebasePoints().pointsAsLatLngL()))

                }.addOnFailureListener {

                    if (routeData != null) {

                        onDataOk?.invoke(routeData!!.toExtendedRouteData(null))
                    } else {

                        onDataFail?.invoke(it)
                    }
                }

    }

    override fun saveRoute(routeData: ExtendedRouteData) {


        val newPointsRef = firebaseFirestore.collection(FIREB_POINTS).document()
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
                .update(newPointsRef, FIREB_ROUTES_ROUTE, newRouteRef)
                .set(userRef.collection(FIREB_ROUTES).document(), FirebaseShortRouteData().apply {
                    fillWithShort(routeData)
                    route = newRouteRef
                    points = newPointsRef
                })
                .commit()
    }

    override fun deleteRoute(routeReference: RouteReference) {

        if (routeReference is FirebaseRouteReference) {

            var batch = firebaseFirestore.batch()

            if (routeReference.userRouteReference != null) {
                batch = batch.delete(routeReference.userRouteReference!!)
            }

            if (routeReference.routeReference != null) {
                batch = batch.delete(routeReference.routeReference!!)
            }

            if (routeReference.pointsReference != null) {
                batch = batch.delete(routeReference.pointsReference!!)
            }

            batch.commit()

        } else {

            throw IllegalArgumentException(WRONG_REFERENCE_MESSAGE)
        }
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
        const val FIREB_ROUTES_ROUTE = "route"
        const val DEFAULT_LIMIT = 10L
        const val FIREB_POINTS = "points"
        const val WRONG_REFERENCE_MESSAGE =
                "routeReference must be FirebaseRouteReference! Probably it doesn't come from FirebaseRouteManager."
    }
}