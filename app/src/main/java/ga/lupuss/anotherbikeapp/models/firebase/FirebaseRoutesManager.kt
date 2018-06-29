package ga.lupuss.anotherbikeapp.models.firebase


import android.app.Activity
import com.google.android.gms.maps.model.LatLng
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
import ga.lupuss.anotherbikeapp.models.firebase.pojo.FirebaseRouteReference
import timber.log.Timber
import java.lang.IllegalArgumentException
import java.util.*


class FirebaseRoutesManager(
        private val firebaseAuth: FirebaseAuth,
        private val firebaseFirestore: FirebaseFirestore,
        private val routeKeeper: TempRouteKeeper,
        private val locale: Locale,
        gson: Gson,
        private val queryManager: QueryLoadingManager = QueryLoadingManager()

): RoutesManager {

    private val onRoutesChangedListeners = mutableListOf<OnDocumentChanged>()
    private val userPath = "$FIREB_USERS/${firebaseAuth.currentUser!!.uid}"
    private val routesPath = "$userPath/$FIREB_ROUTES"
    private val routesQuery = firebaseFirestore
            .collection(routesPath)
            .orderBy(FIREB_START_TIME, Query.Direction.DESCENDING)

    init {

        queryManager.init(routesQuery, onRoutesChangedListeners)
    }


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

    override fun requestMoreShortRouteData(onRequestMoreShortRouteDataListener: RoutesManager.OnRequestMoreShortRouteDataListener?, requestOwner: Any?) {

        if (requestOwner !is Activity)
            throw IllegalArgumentException("Request owner should be an activity!")

        queryManager.loadMoreDocuments(
                { onRequestMoreShortRouteDataListener?.onDataEnd() },
                { onRequestMoreShortRouteDataListener?.onFail(it) },
                requestOwner
        )
    }

    override fun readShortRouteData(position: Int): ShortRouteData {

        return queryManager.readDocument(position)
                .toFirebaseShortRouteData()
                .toShortRouteData()
    }

    override fun shortRouteDataCount() = queryManager.size

    override fun getRouteReference(position: Int): RouteReference {

        val shortRouteDataSnap = queryManager.readDocument(position)
        val shortRouteData = shortRouteDataSnap.toFirebaseShortRouteData()

        return FirebaseRouteReference(shortRouteDataSnap.reference, shortRouteData.route, shortRouteData.points)
    }

    override fun requestExtendedRoutesData(
            routeReference: RouteReference,
            onRequestExtendedRouteDataListener: RoutesManager.OnRequestExtendedRouteDataListener?,
            requestOwner: Any?) {

        if (requestOwner !is Activity)
            throw IllegalArgumentException(WRONG_OWNER)

        if (routeReference !is FirebaseRouteReference)
            throw IllegalArgumentException(WRONG_REFERENCE_MESSAGE)

        if (routeReference.routeReference == null) {
            onRequestExtendedRouteDataListener?.onMissingData()
            return
        }

        if (routeReference.pointsReference == null)
            Timber.w("Points reference is null!")


        var routeData: RouteData? = null

        fun checkRouteDataAndPostResult(points: MutableList<LatLng>?) {

            if (routeData != null) {

                onRequestExtendedRouteDataListener?.onDataOk(
                        routeData!!.toExtendedRouteData(points)
                )

            } else {

                onRequestExtendedRouteDataListener?.onMissingData()
            }
        }

        routeReference.routeReference!!.get()
                .continueWithTask {

                    if (it.result.exists()) {

                        routeData = it.result.toFirebaseRouteData().toRouteData(locale)

                    }

                    routeReference.pointsReference?.get()
                }.addOnSuccessListener(requestOwner) {

                    val points = if (it.exists()) it.toFirebasePoints().pointsAsLatLngL() else null

                    checkRouteDataAndPostResult(points)

                }.addOnFailureListener(requestOwner) {

                    Timber.e(it)

                    checkRouteDataAndPostResult(null)

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
                .update(newPointsRef, FIREB_ROUTE, newRouteRef)
                .set(userRef.collection(FIREB_ROUTES).document(), FirebaseShortRouteData().apply {
                    fillWith(routeData)
                    route = newRouteRef
                    points = newPointsRef
                })
                .commit()
    }

    override fun changeName(routeReference: RouteReference, routeNameFromEditText: String) {

        if (routeReference !is FirebaseRouteReference)
            throw IllegalArgumentException(WRONG_REFERENCE_MESSAGE)

        firebaseFirestore
                .batch()
                .update(routeReference.userRouteReference!!, FIREB_NAME, routeNameFromEditText)
                .update(routeReference.routeReference!!, FIREB_NAME, routeNameFromEditText)
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
        const val FIREB_USERS = "users"
        const val FIREB_ROUTES = "routes"
        const val FIREB_START_TIME = "startTime"
        const val FIREB_ROUTE = "route"
        const val DEFAULT_LIMIT = 10L
        const val FIREB_POINTS = "points"
        const val FIREB_NAME = "name"
        const val WRONG_REFERENCE_MESSAGE =
                "routeReference must be FirebaseRouteReference! Probably it doesn't come from FirebaseRouteManager."
        const val WRONG_OWNER = "Request owner should be an activity!"
    }
}