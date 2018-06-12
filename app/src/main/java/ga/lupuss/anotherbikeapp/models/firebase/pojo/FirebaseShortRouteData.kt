package ga.lupuss.anotherbikeapp.models.firebase.pojo

import com.google.firebase.firestore.DocumentReference
import ga.lupuss.anotherbikeapp.models.dataclass.MutableShortRouteData
import ga.lupuss.anotherbikeapp.models.dataclass.ShortRouteData

class FirebaseShortRouteData : MutableShortRouteData() {

    var route: DocumentReference? = null
    var points: DocumentReference? = null

    fun toShortRouteData() = ShortRouteData(
            name, distance, avgSpeed, duration, startTime
    )

}