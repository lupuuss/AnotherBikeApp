package ga.lupuss.anotherbikeapp.models.firebase.pojo

import com.google.firebase.firestore.Exclude
import ga.lupuss.anotherbikeapp.models.dataclass.ShortRouteData

open class FirebaseBaseRouteData : ShortRouteData() {

    var id: String? = null
        @Exclude
        get
        @Exclude
        set
}