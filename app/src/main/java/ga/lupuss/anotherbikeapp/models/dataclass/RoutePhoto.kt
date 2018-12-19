package ga.lupuss.anotherbikeapp.models.dataclass

data class RoutePhoto(
        val link: String,
        val name: String?,
        val time: Long
) {

    // Firebase uses no-argument constructor
    constructor(): this("", "", 0)
}