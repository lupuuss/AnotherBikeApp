package ga.lupuss.anotherbikeapp.models.firebase.pojo

import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.GeoPoint

class FirebasePoints {

    var points: List<GeoPoint>? = null
    var route: DocumentReference? = null

}