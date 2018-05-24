package ga.lupuss.anotherbikeapp.models.firebase.pojo

import com.google.firebase.firestore.DocumentReference
import ga.lupuss.anotherbikeapp.models.pojo.RouteData
import ga.lupuss.anotherbikeapp.timeToFormattedString
import java.util.*

class FirebaseRouteData() {
    var name = ""
    var distance = 0.0
    var duration = 0L
    var avgSpeed = 0.0
    var maxSpeed = 0.0
    var startTime = 0L
    var points: DocumentReference? = null
    var user: DocumentReference? = null

    fun toRouteData(locale: Locale): RouteData {

        return RouteData(
                name,
                distance,
                avgSpeed,
                maxSpeed,
                duration,
                timeToFormattedString(locale, startTime),
                startTime
        )
    }
}