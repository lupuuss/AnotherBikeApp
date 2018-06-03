package ga.lupuss.anotherbikeapp.models.interfaces

interface RouteReferenceSerializer {
    fun serialize(routeReference: RouteReference): String
    fun deserialize(stringState: String): RouteReference
}