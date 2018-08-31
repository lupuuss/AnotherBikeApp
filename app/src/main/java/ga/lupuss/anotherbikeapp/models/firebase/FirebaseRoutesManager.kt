package ga.lupuss.anotherbikeapp.models.firebase


import android.app.Activity
import android.support.v4.app.Fragment
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.*
import com.google.gson.Gson
import ga.lupuss.anotherbikeapp.models.base.AuthInteractor
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
        private val authInteractor: AuthInteractor,
        private val firebaseFirestore: FirebaseFirestore,
        private val routeKeeper: TempRouteKeeper,
        private val locale: Locale,
        gson: Gson

): RoutesManager, OnDataSetChanged{

    private val children = mutableListOf<DocumentSnapshot>()

    private val limit: Long = DEFAULT_LIMIT

    private val onRoutesChangedListeners = mutableListOf<OnDataSetChanged>()
    private val userPath = "$FIREB_USERS/${authInteractor.userUid!!}"
    private val routesPath = "$userPath/$FIREB_ROUTES"
    private val routesQuery = firebaseFirestore
            .collection(routesPath)
            .orderBy(FIREB_START_TIME, Query.Direction.DESCENDING)


    private fun DocumentSnapshot.toFirebaseShortRouteData(): FirebaseShortRouteData =
            this.toObject(FirebaseShortRouteData::class.java)!!.apply {
                reference = this@toFirebaseShortRouteData.reference
            }

    private fun DocumentSnapshot.toFirebaseRouteData(): FirebaseRouteData =
            this.toObject(FirebaseRouteData::class.java)!!

    private fun DocumentSnapshot.toFirebasePoints(): FirebasePoints =
            this.toObject(FirebasePoints::class.java)!!

    override val routeReferenceSerializer: RouteReferenceSerializer = FirebaseRouteReferenceSerializer(gson, firebaseFirestore)

    override fun onNewDocument(position: Int) {

        onRoutesChangedListeners.forEach { it.onNewDocument(position) }
    }

    override fun onDocumentDeleted(position: Int) {

        onRoutesChangedListeners.forEach { it.onDocumentDeleted(position) }
    }

    override fun onDocumentModified(position: Int) {

        onRoutesChangedListeners.forEach { it.onDocumentModified(position) }
    }

    override fun onDataSetChanged() {

        onRoutesChangedListeners.forEach { it.onDataSetChanged() }
    }

    override fun addRoutesDataChangedListener(onRoutesChangedListener: OnDataSetChanged) {

        onRoutesChangedListeners.add(onRoutesChangedListener)
    }

    override fun removeOnRoutesDataChangedListener(onRoutesChangedListener: OnDataSetChanged) {

        onRoutesChangedListeners.remove(onRoutesChangedListener)
    }

    override fun requestMoreShortRouteData(onRequestMoreShortRouteDataListener: RoutesManager.OnRequestMoreShortRouteDataListener?, requestOwner: Any?) {

        var owner = requestOwner

        if (owner is Fragment) {

            owner = owner.requireActivity()
        }

        if (owner !is Activity)
            throw IllegalArgumentException("Request owner should be an activity!")

        val query = if (children.isNotEmpty()) {

            routesQuery
                    .startAfter(children.last())
                    .limit(limit)

        } else {

            routesQuery.limit(limit)
        }

        query.get()
                .addOnSuccessListener(owner) {

                    if (it.isEmpty) {

                        onRequestMoreShortRouteDataListener?.onDataEnd()

                    } else {
                        it.documents.forEach {
                            children.add(it)
                            onNewDocument(children.size - 1)
                        }

                        if (it.documents.size < limit) {

                            onRequestMoreShortRouteDataListener?.onDataEnd()
                        }
                    }

                    onRequestMoreShortRouteDataListener?.onRequestSuccess()
                }
                .addOnFailureListener(owner) {
                    onRequestMoreShortRouteDataListener?.onFail(it)
                }

    }

    override fun refresh(onRequestMoreShortRouteDataListener: RoutesManager.OnRequestMoreShortRouteDataListener?, requestOwner: Any?) {
        children.clear()
        onRoutesChangedListeners.forEach { it.onDataSetChanged() }

        requestMoreShortRouteData(onRequestMoreShortRouteDataListener, requestOwner)
    }

    override fun readShortRouteData(position: Int): ShortRouteData {

        return children[position].toFirebaseShortRouteData()
    }

    override fun shortRouteDataCount() = children.size

    override fun getRouteReference(position: Int): RouteReference {

        val shortRouteDataSnap = children[position]
        val shortRouteData = children[position].toFirebaseShortRouteData()

        return FirebaseRouteReference(shortRouteDataSnap.reference, shortRouteData.route, shortRouteData.points, position)
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

                    val points = if (it.exists()) it.toFirebasePoints().pointsAsLatLngList() else null

                    checkRouteDataAndPostResult(points)

                }.addOnFailureListener(requestOwner) {

                    Timber.e(it)

                    checkRouteDataAndPostResult(null)

                }

    }

    override fun saveRoute(routeData: ExtendedRouteData) {


        val newPointsRef = firebaseFirestore.collection(FIREB_POINTS).document()
        val newRouteRef = firebaseFirestore.collection(FIREB_ROUTES).document()
        val userRef = firebaseFirestore.collection(FIREB_USERS).document(authInteractor.userUid!!)
        val newUserRefRoute = userRef.collection(FIREB_ROUTES).document()

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
                .set(newUserRefRoute, FirebaseShortRouteData().apply {
                    fillWith(routeData)
                    route = newRouteRef
                    points = newPointsRef
                })
                .commit()
                .continueWithTask {

                    newUserRefRoute.get()

                }.addOnSuccessListener {

                    children.add(0, it)
                    onNewDocument(0)
                }
    }

    override fun changeName(routeReference: RouteReference, routeNameFromEditText: String) {

        if (routeReference !is FirebaseRouteReference)
            throw IllegalArgumentException(WRONG_REFERENCE_MESSAGE)

        firebaseFirestore
                .batch()
                .update(routeReference.userRouteReference!!, FIREB_NAME, routeNameFromEditText)
                .update(routeReference.routeReference!!, FIREB_NAME, routeNameFromEditText)
                .commit()
                .continueWithTask {

                    routeReference.userRouteReference!!.get()
                }.addOnSuccessListener {
                    children[routeReference.localIndex] = it
                    onDocumentModified(routeReference.localIndex)
                }
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
                    .addOnSuccessListener {
                        children.removeAt(routeReference.localIndex)
                        onDocumentDeleted(routeReference.localIndex)
                    }

        } else {

            throw IllegalArgumentException(WRONG_REFERENCE_MESSAGE)
        }
    }

    override fun keepTempRoute(slot: RoutesManager.Slot, routeData: ExtendedRouteData) {

        routeKeeper.keep(slot, routeData)
    }

    override fun getTempRoute(slot: RoutesManager.Slot): ExtendedRouteData? = routeKeeper.getRoute(slot)

    override fun clearTempRoute(slot: RoutesManager.Slot) {

        routeKeeper.clear(slot)
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