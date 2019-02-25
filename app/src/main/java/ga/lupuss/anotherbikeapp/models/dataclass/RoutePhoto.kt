package ga.lupuss.anotherbikeapp.models.dataclass

import com.google.firebase.firestore.Exclude

data class RoutePhoto(
        val link: String,
        val name: String?,
        val time: Long
) {

    @get:Exclude @set:Exclude
    var id: String? = null
    // Firebase uses no-argument constructor
    constructor(): this("", "", 0)
}