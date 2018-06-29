package ga.lupuss.anotherbikeapp.models.dataclass

interface MutableRouteData : RouteData, MutableShortRouteData {

    override var name: String?
    override var distance: Double
    override var avgSpeed: Double
    override var maxSpeed: Double
    override var duration: Long
    override var startTimeStr: String
    override var startTime: Long
    override var avgAltitude: Double
    override var maxAltitude: Double
    override var minAltitude: Double

    open class Instance(
            override var name: String? = null,
            override var distance: Double = 0.0,
            override var avgSpeed: Double = 0.0,
            override var maxSpeed: Double = 0.0,
            override var duration: Long = 0L,
            override var startTimeStr: String = "",
            override var startTime: Long = 0L,
            override var avgAltitude: Double = 0.0,
            override var maxAltitude: Double = 0.0,
            override var minAltitude: Double = 0.0
    ) : MutableRouteData
}