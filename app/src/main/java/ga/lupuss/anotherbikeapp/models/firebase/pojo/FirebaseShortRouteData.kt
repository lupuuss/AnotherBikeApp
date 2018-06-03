package ga.lupuss.anotherbikeapp.models.firebase.pojo

import com.google.firebase.firestore.DocumentReference
import ga.lupuss.anotherbikeapp.models.dataclass.ShortRouteData

class FirebaseShortRouteData : ShortRouteData() {

    var route: DocumentReference? = null
    var points: DocumentReference? = null
}