package ga.lupuss.anotherbikeapp.models.firebase.pojo

import com.google.firebase.firestore.DocumentReference
import ga.lupuss.anotherbikeapp.models.pojo.ShortRouteData

class FirebaseShortRouteData : FirebaseBaseRouteData() {

    var more: DocumentReference? = null
}