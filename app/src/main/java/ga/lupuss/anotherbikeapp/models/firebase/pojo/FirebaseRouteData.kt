package ga.lupuss.anotherbikeapp.models.firebase.pojo

import com.google.firebase.firestore.DocumentReference
import ga.lupuss.anotherbikeapp.models.dataclass.MutableRouteData
import ga.lupuss.anotherbikeapp.models.dataclass.MutableShortRouteData
import ga.lupuss.anotherbikeapp.models.dataclass.RouteData
import ga.lupuss.anotherbikeapp.timeToFormattedString
import java.util.*

class FirebaseRouteData : MutableRouteData.Instance() {

    var points: DocumentReference? = null
    var user: DocumentReference? = null

    fun fillWith(routeData: RouteData) {

        super.fillWith(routeData)
        maxSpeed = routeData.maxSpeed
        avgAltitude = routeData.avgAltitude
        maxAltitude = routeData.maxAltitude
        minAltitude = routeData.minAltitude
    }

    fun toRouteData(locale: Locale): RouteData {

        return RouteData.Instance(
                name = name,
                distance = distance,
                avgSpeed = avgSpeed,
                maxSpeed = maxSpeed,
                duration = duration,
                startTimeStr = timeToFormattedString(locale, startTime),
                startTime = startTime,
                avgAltitude = avgAltitude,
                maxAltitude = maxAltitude,
                minAltitude = minAltitude
        )
    }
}