package ga.lupuss.anotherbikeapp.models.base

interface RouteReferenceSerializer {
    fun serialize(routeReference: RouteReference): String
    fun deserialize(stringState: String): RouteReference
}