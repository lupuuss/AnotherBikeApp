package ga.lupuss.anotherbikeapp.models.firebase


import android.app.Activity
import androidx.fragment.app.Fragment
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.*
import com.google.firebase.storage.StorageReference
import com.google.gson.Gson
import ga.lupuss.anotherbikeapp.models.base.*
import ga.lupuss.anotherbikeapp.models.dataclass.*
import ga.lupuss.anotherbikeapp.models.firebase.pojo.FirebasePoints
import ga.lupuss.anotherbikeapp.models.firebase.pojo.FirebaseRouteData
import ga.lupuss.anotherbikeapp.models.firebase.pojo.FirebaseShortRouteData
import ga.lupuss.anotherbikeapp.models.firebase.pojo.FirebaseRouteReference
import timber.log.Timber
import java.io.File
import java.lang.IllegalArgumentException
import java.util.*


class FirebaseRoutesManager(
        private val authInteractor: AuthInteractor,
        private val firebaseFirestore: FirebaseFirestore,
        private val routeKeeper: TempRouteKeeper,
        private val locale: Locale,
        photosSynchronizer: PhotosSynchronizer,
        gson: Gson

): RoutesManager, OnDataSetChanged{

    private val photosSynchronizer: FirebasePhotosSynchronizer = photosSynchronizer as FirebasePhotosSynchronizer
    private val children = mutableListOf<DocumentSnapshot>()

    private val limit: Long = DEFAULT_LIMIT

    private val onRoutesChangedListeners = mutableListOf<OnDataSetChanged>()
    private val userPath = "$FIREB_USERS/${authInteractor.userUid!!}"
    private val routesPath = "$userPath/$FIREB_ROUTES"
    private val routesQuery = firebaseFirestore
            .collection(routesPath)
            .orderBy(FIREB_START_TIME, Query.Direction.DESCENDING)


    private fun DocumentSnapshot.toFirebaseShortRouteData(): FirebaseShortRouteData =
            this.toObject(FirebaseShortRouteData::class.java)!!

    private fun DocumentSnapshot.toFirebaseRouteData(): FirebaseRouteData =
            this.toObject(FirebaseRouteData::class.java)!!

    private fun DocumentSnapshot.toFirebasePoints(): FirebasePoints =
            this.toObject(FirebasePoints::class.java)!!

    private fun DocumentSnapshot.toRoutePhoto(routeId: String): MarkedRoutePhoto {
        val photo = this.toObject(RoutePhoto::class.java)!!
        return photo.mark(id, routeId)
    }

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
                .addOnSuccessListener(owner) { querySnapshot ->

                    if (querySnapshot.isEmpty) {

                        onRequestMoreShortRouteDataListener?.onDataEnd()

                    } else {
                        querySnapshot.documents.forEach {
                            children.add(it)
                            onNewDocument(children.size - 1)
                        }

                        if (querySnapshot.documents.size < limit) {

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


        val routeReference: DocumentReference? = if (shortRouteData.routeId != null) {
            firebaseFirestore.collection(FIREB_ROUTES).document(shortRouteData.routeId!!)
        } else {
            null
        }

        val pointsReference: DocumentReference? = if (shortRouteData.pointsId != null) {
            firebaseFirestore.collection(FIREB_POINTS).document(shortRouteData.pointsId!!)
        } else {
            null
        }

        val photosReference: CollectionReference =
                firebaseFirestore.collection("${routeReference!!.path}/photos")

        return FirebaseRouteReference(
                shortRouteDataSnap.reference,
                routeReference,
                pointsReference,
                photosReference,
                position
        )
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
        var points: MutableList<LatLng>? = null
        var photos: MutableList<RoutePhoto>? = null

        fun checkOut() {

            if (routeData != null) {

                onRequestExtendedRouteDataListener?.onDataOk(
                        routeData!!.toExtendedRouteData(points, photos)
                )

            } else {

                onRequestExtendedRouteDataListener?.onMissingData()
            }
        }

        routeReference.routeReference!!.get()
                .continueWithTask {

                    if (it.result!!.exists()) {

                        routeData = it.result!!.toFirebaseRouteData().toRouteData(locale)

                    }

                    routeReference.pointsReference?.get()
                }.continueWithTask {

                    points = if (it.result!!.exists()) {

                        it.result!!.toFirebasePoints().pointsAsLatLngList()
                    } else {

                        null
                    }

                    routeReference.photosReference?.get()
                }.addOnSuccessListener(requestOwner) { query ->

                    photos = if (!query.isEmpty) {

                        query.documents.map { it.toRoutePhoto(routeReference.routeReference!!.id) }
                                .toMutableList()
                    } else {

                        null
                    }

                    checkOut()

                }.addOnFailureListener(requestOwner) {

                    Timber.e(it)

                    checkOut()

                }

    }

    override fun removePhoto(routePhoto: MarkedRoutePhoto, routeReference: RouteReference) {

        if (routeReference !is FirebaseRouteReference)
            throw IllegalArgumentException(WRONG_REFERENCE_MESSAGE)
        else {

            photosSynchronizer.rejectUpload(routePhoto)

            routeReference.routeReference!!
                    .collection(FIREB_PHOTOS)
                    .document(routePhoto.id)
                    .delete().addOnFailureListener {

                        Timber.e(it)
                    }
        }


    }

    override fun removePhotoFile(routePhoto: RoutePhoto) {
        photosSynchronizer.removeFile(routePhoto)
    }

    override fun cancelAllPhotosUpload() {
        photosSynchronizer.cancelAllUploads()
    }

    override fun saveRoute(routeData: ExtendedRouteData) {


        val newPointsRef = firebaseFirestore.collection(FIREB_POINTS).document()
        val newRouteRef = firebaseFirestore.collection(FIREB_ROUTES).document()
        val userRef = firebaseFirestore.collection(FIREB_USERS).document(authInteractor.userUid!!)
        val newUsersRouteRef = userRef.collection(FIREB_ROUTES).document()

        val markedPhotos = mutableListOf<MarkedRoutePhoto>()

        val batch = firebaseFirestore
                .batch()
                .set(newPointsRef, FirebasePoints().apply {
                    this.points = routeData.points.map { GeoPoint(it.latitude, it.longitude) }
                })
                .set(newRouteRef, FirebaseRouteData().apply {
                    fillWith(routeData)
                    pointsId = newPointsRef.id
                    userId = userRef.id
                })
                .update(newPointsRef, FIREB_ROUTE_ID, newRouteRef.id)
                .update(newPointsRef, FIREB_USER_ID, userRef.id)
                .set(newUsersRouteRef, FirebaseShortRouteData().apply {
                    fillWith(routeData)
                    routeId = newRouteRef.id
                    pointsId = newPointsRef.id
                })

        routeData.photos.forEach {

            val doc = newRouteRef.collection(FIREB_PHOTOS).document()
            markedPhotos.add(it.mark(doc.id, newRouteRef.id))
            batch.set(doc, it)
        }

        batch.commit()
                .addOnFailureListener {
                    Timber.e(it)
                }

        newUsersRouteRef
                .get()
                .addOnSuccessListener {

                    if (!children.contains(it)) {

                        children.add(0, it)
                        onNewDocument(0)
                    }

                    photosSynchronizer.uploadAll(markedPhotos)

                }.addOnFailureListener {

                    Timber.e(it)
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

        routeReference.userRouteReference!!
                .get()
                .addOnSuccessListener {
                    children[routeReference.localIndex] = it
                    onDocumentModified(routeReference.localIndex)
                }
    }

    override fun deleteRoute(routeReference: RouteReference) {

        if (routeReference is FirebaseRouteReference) {


            var batch = firebaseFirestore.batch()


            routeReference.routeReference?.let {
                photosSynchronizer.rejectAllUploadsForRoute(it.id)
                batch = batch.delete(it)
            }

            routeReference.userRouteReference?.let {
                batch = batch.delete(it)
            }

            routeReference.pointsReference?.let {
                batch = batch.delete(it)
            }

            batch.commit()
                    .addOnFailureListener {
                        Timber.e(it)
                    }

            children.removeAt(routeReference.localIndex)
            onDocumentDeleted(routeReference.localIndex)

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

    fun getStoragePhotoReference(link: String): StorageReference =
            photosSynchronizer.getStorageReference(link)

    override fun getImageReference(routePhoto: RoutePhoto): ImageReference {

        val file = getPathForRoutePhoto(routePhoto)

        return if (file.exists()) {

            ImageReference.Local(file)

        } else {

            ImageReference.Firebase(getStoragePhotoReference(routePhoto.link))
        }
    }

    override fun getPathForRoutePhoto(photo: RoutePhoto): File {

        return photosSynchronizer.getPathForPhotoLink(photo.link)
    }

    companion object {

        // Firebase path consts starts with FIREB
        const val FIREB_USERS = "users"
        const val FIREB_ROUTES = "routes"
        const val FIREB_START_TIME = "startTime"
        const val FIREB_PHOTOS = "photos"
        const val DEFAULT_LIMIT = 10L
        const val FIREB_POINTS = "points"
        const val FIREB_ROUTE_ID = "routeId"
        const val FIREB_USER_ID = "userId"
        const val FIREB_NAME = "name"
        const val WRONG_REFERENCE_MESSAGE =
                "routeReference must be FirebaseRouteReference! Probably it doesn't come from FirebaseRouteManager."
        const val WRONG_OWNER = "Request owner should be an activity!"
    }
}
