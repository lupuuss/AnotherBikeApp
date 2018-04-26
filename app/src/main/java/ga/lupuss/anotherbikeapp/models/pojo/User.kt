package ga.lupuss.anotherbikeapp.models.pojo

import java.io.File

data class User(
        val name: String,
        val routesPath: String,
        var savedRouteData: List<File>
)