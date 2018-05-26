package ga.lupuss.anotherbikeapp.models.firebase.pojo

import com.google.firebase.firestore.DocumentReference
import ga.lupuss.anotherbikeapp.models.pojo.RouteData
import ga.lupuss.anotherbikeapp.models.pojo.ShortRouteData
import ga.lupuss.anotherbikeapp.timeToFormattedString
import java.util.*

class FirebaseRouteData() : FirebaseBaseRouteData() {

    var maxSpeed = 0.0
    var points: DocumentReference? = null
    var user: DocumentReference? = null

    fun toRouteData(locale: Locale): RouteData {

        return RouteData(
                name = name,
                distance = distance,
                avgSpeed = avgSpeed,
                maxSpeed = maxSpeed,
                duration = duration,
                startTimeStr = timeToFormattedString(locale, startTime),
                startTime = startTime
        )
    }
}