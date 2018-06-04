package ga.lupuss.anotherbikeapp.models.firebase

import com.google.firebase.firestore.DocumentReference
import ga.lupuss.anotherbikeapp.models.base.RouteReference

class FirebaseRouteReference(
        var userRouteReference: DocumentReference?,
        var routeReference: DocumentReference?,
        var pointsReference: DocumentReference?
) : RouteReference
