package ga.lupuss.anotherbikeapp.models.pojo

import ga.lupuss.anotherbikeapp.DEFAULT_PROFILE_NAME
import java.io.File

data class User(
        val name: String,
        val unsyncRoutesPath: File,
        val syncRoutesPath: File,
        var savedRouteData: List<File>,
        val isDefault: Boolean
) {
    companion object {

        @JvmStatic
        lateinit var defaultUser: User
    }
}