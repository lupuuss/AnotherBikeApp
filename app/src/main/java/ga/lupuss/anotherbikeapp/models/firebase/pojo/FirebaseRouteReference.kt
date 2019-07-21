package ga.lupuss.anotherbikeapp.models.firebase.pojo

import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentReference
import ga.lupuss.anotherbikeapp.models.base.RouteReference

class FirebaseRouteReference(
        var userRouteReference: DocumentReference?,
        var routeReference: DocumentReference?,
        var pointsReference: DocumentReference?,
        var photosReference: CollectionReference?,
        var localIndex: Int
) : RouteReference
