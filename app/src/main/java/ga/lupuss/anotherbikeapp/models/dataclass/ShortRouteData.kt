package ga.lupuss.anotherbikeapp.models.dataclass

interface ShortRouteData {

    val name: String?
    val distance: Double
    val avgSpeed: Double
    val duration: Long
    val startTime: Long

    open class Instance(
            override val name: String?,
            override val distance: Double,
            override val avgSpeed: Double,
            override val duration: Long,
            override val startTime: Long
    ) : ShortRouteData
}
