package ga.lupuss.anotherbikeapp.models.firebase

import com.google.firebase.firestore.DocumentReference
import ga.lupuss.anotherbikeapp.models.interfaces.RouteReference
import org.w3c.dom.Document

class FirebaseRouteReference(
        var userRouteReference: DocumentReference?,
        var routeReference: DocumentReference?,
        var pointsReference: DocumentReference?
) : RouteReference
