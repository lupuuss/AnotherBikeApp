package ga.lupuss.anotherbikeapp.models.pojo

import com.google.gson.annotations.Expose
import ga.lupuss.anotherbikeapp.models.PathsGenerator
import java.io.File

data class User(
        @Expose
        val name: String,
        @Expose
        val isDefault: Boolean,
        @Expose(serialize = false)
        val unsyncRoutesPath: File = File(""),
        @Expose(serialize = false)
        val syncRoutesPath: File = File(""),
        @Expose(serialize = false)
        var savedRouteData: List<File> = listOf()
) {
    companion object {

        @JvmStatic
        lateinit var defaultUser: User

        fun newInstance(pathsGenerator: PathsGenerator,
                        name: String,
                        isDefault: Boolean): User {

            return if (!isDefault) {

                val pathUnsync = pathsGenerator.generateUnsyncRoutesPathForUserName(name)
                val pathSync = pathsGenerator.generateSyncRoutesPathForUserName(name)

                User(
                        name,
                        false,
                        pathUnsync,
                        pathSync
                )
            } else {

                defaultUser
            }
        }
    }
}