package ga.lupuss.anotherbikeapp.models.firebase.pojo

import com.google.firebase.firestore.DocumentReference
import ga.lupuss.anotherbikeapp.models.dataclass.MutableShortRouteData
import ga.lupuss.anotherbikeapp.models.dataclass.RouteData
import ga.lupuss.anotherbikeapp.models.dataclass.ShortRouteData
import ga.lupuss.anotherbikeapp.timeToFormattedString
import java.util.*

class FirebaseRouteData : MutableShortRouteData() {

    var maxSpeed = 0.0
    var points: DocumentReference? = null
    var user: DocumentReference? = null

    fun fillWith(routeData: RouteData) {

        super.fillWith(routeData)
        maxSpeed = routeData.maxSpeed
    }

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