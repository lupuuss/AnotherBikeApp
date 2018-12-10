package ga.lupuss.anotherbikeapp.models.dataclass

import com.google.firebase.firestore.Exclude

interface MutableRouteData : RouteData, MutableShortRouteData {

    override var startTimeStr: String
    override var maxSpeed: Double
    override var avgAltitude: Double
    override var maxAltitude: Double
    override var minAltitude: Double
    override var photos: MutableList<RoutePhoto>

    open class Instance(
            override var name: String? = null,
            override var distance: Double = 0.0,
            override var avgSpeed: Double = 0.0,
            override var maxSpeed: Double = 0.0,
            override var duration: Long = 0L,
            @get:Exclude @set:Exclude
            override var startTimeStr: String = "",
            override var startTime: Long = 0L,
            override var avgAltitude: Double = 0.0,
            override var maxAltitude: Double = 0.0,
            override var minAltitude: Double = 0.0,
            override var photos: MutableList<RoutePhoto> = mutableListOf()
    ) : MutableRouteData
}