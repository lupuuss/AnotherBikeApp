package ga.lupuss.anotherbikeapp.models.firebase

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.gson.Gson
import ga.lupuss.anotherbikeapp.models.base.RouteReference
import ga.lupuss.anotherbikeapp.models.base.RouteReferenceSerializer
import ga.lupuss.anotherbikeapp.models.firebase.pojo.FirebaseRouteReference

class FirebaseRouteReferenceSerializer(private val gson: Gson,
                                       private val firestore: FirebaseFirestore) : RouteReferenceSerializer {

    private class SerializableReference {
        var userRoute: String? = null
        var route: String? = null
        var points: String? = null
        var photos: String? = null
        var localIndex: Int = -1
    }

    override fun serialize(routeReference: RouteReference): String {

        if (routeReference !is FirebaseRouteReference)
            throw IllegalStateException(FirebaseRoutesManager.WRONG_REFERENCE_MESSAGE)

        val serializableReference = SerializableReference().apply {
            userRoute = routeReference.userRouteReference?.path
            route = routeReference.routeReference?.path
            points = routeReference.pointsReference?.path
            photos = routeReference.photosReference?.path
            localIndex = routeReference.localIndex
        }

        return gson.toJson(serializableReference, SerializableReference::class.java)
    }

    override fun deserialize(stringState: String): RouteReference {

        val serializableReference = gson.fromJson(stringState, SerializableReference::class.java)

        val userRoute: DocumentReference? = if (serializableReference.userRoute != null)
            firestore.document(serializableReference.userRoute!!) else null

        val route: DocumentReference? = if (serializableReference.route != null)
            firestore.document(serializableReference.route!!) else null

        val points: DocumentReference? = if (serializableReference.userRoute != null)
            firestore.document(serializableReference.points!!) else null

        val photos: CollectionReference? = if (serializableReference.photos != null)
            firestore.collection(serializableReference.photos!!) else null

        return FirebaseRouteReference(
                userRoute,
                route,
                points,
                photos,
                serializableReference.localIndex
        )
    }
}