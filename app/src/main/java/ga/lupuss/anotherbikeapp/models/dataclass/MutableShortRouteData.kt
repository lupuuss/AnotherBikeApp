package ga.lupuss.anotherbikeapp.models.dataclass

interface MutableShortRouteData : ShortRouteData {
    override var name: String?
    override var distance: Double
    override var avgSpeed: Double
    override var duration: Long
    override var startTime: Long

    fun fillWith(routeData: ShortRouteData) {
        this.name = routeData.name
        this.distance = routeData.distance
        this.avgSpeed = routeData.avgSpeed
        this.duration = routeData.duration
        this.startTime = routeData.startTime
    }

    open class Instance : MutableShortRouteData {
        override var name: String? = null
        override var distance: Double = 0.0
        override var avgSpeed: Double = 0.0
        override var duration: Long = 0L
        override var startTime: Long = 0L
    }
}