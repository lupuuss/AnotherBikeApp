package ga.lupuss.anotherbikeapp.models.dataclass

open class RoutePhoto(
        val link: String,
        val name: String?,
        val time: Long
) {

    // non argument contstructor for Firebase
    constructor() : this("", "", 0)

    fun mark(id: String, routeId: String) = MarkedRoutePhoto(link, name, time, routeId, id)
}

class MarkedRoutePhoto(
        link: String,
        name: String?,
        time: Long,
        val routeId: String,
        val id: String
) : RoutePhoto(link, name, time)