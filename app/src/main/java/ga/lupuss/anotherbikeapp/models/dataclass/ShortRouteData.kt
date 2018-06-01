package ga.lupuss.anotherbikeapp.models.dataclass

open class ShortRouteData() {

    var name: String? = null
    var distance: Double = 0.0
    var avgSpeed: Double = 0.0
    var duration: Long = 0L
    var startTime: Long = 0L

    constructor(
            name: String?,
            distance: Double,
            avgSpeed: Double,
            duration: Long,
            startTime: Long
    ) : this() {
        this.name = name
        this.distance = distance
        this.avgSpeed = avgSpeed
        this.duration = duration
        this.startTime = startTime
    }

    fun fillWithShort(routeData: ShortRouteData) {
        this.name = routeData.name
        this.distance = routeData.distance
        this.avgSpeed = routeData.avgSpeed
        this.duration = routeData.duration
        this.startTime = routeData.startTime
    }
}
