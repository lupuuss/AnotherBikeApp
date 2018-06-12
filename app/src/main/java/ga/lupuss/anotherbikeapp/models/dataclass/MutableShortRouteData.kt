package ga.lupuss.anotherbikeapp.models.dataclass

open class MutableShortRouteData {
    var name: String? = null
    var distance: Double = 0.0
    var avgSpeed: Double = 0.0
    var duration: Long = 0L
    var startTime: Long = 0L

    fun fillWith(routeData: ShortRouteData) {
        this.name = routeData.name
        this.distance = routeData.distance
        this.avgSpeed = routeData.avgSpeed
        this.duration = routeData.duration
        this.startTime = routeData.startTime
    }
}