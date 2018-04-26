package ga.lupuss.anotherbikeapp.models

import android.content.Context
import ga.lupuss.anotherbikeapp.AnotherBikeApp
import ga.lupuss.anotherbikeapp.ROUTE_FILE_PREFIX
import ga.lupuss.anotherbikeapp.TEMP_ROUTE_FILE_PREFIX
import ga.lupuss.anotherbikeapp.models.pojo.SerializableRouteData
import ga.lupuss.anotherbikeapp.models.pojo.User
import java.io.File

class RoutesKeeper(private val filesManager: FilesManager, private val context: Context) {

    fun saveRoute(user: User, routeData: SerializableRouteData) {

        user.savedRouteData = filesManager.makeChildrenListFor(user.routesPath)
        filesManager.saveObjectToFile(routeData, generatePath(user).toString())
        user.savedRouteData = filesManager.makeChildrenListFor(user.routesPath)
    }

    fun saveRouteTemporary(routeData: SerializableRouteData): File {

        return filesManager.saveObjectToFile(
                routeData,
                File.createTempFile(TEMP_ROUTE_FILE_PREFIX, "json", context.cacheDir)
                        .absolutePath
        ).apply {
            deleteOnExit()
        }
    }

    fun readRoute(path: String): SerializableRouteData {
        return filesManager.readFileToObject(path, SerializableRouteData::class.java)
    }

    fun routesFilesListForUser(user: User): List<File> {

        return routesFilesListForUser(user.name)
    }

    fun routesFilesListForUser(userName: String): List<File> {
        return filesManager.makeChildrenListFor(routesPathForUser(userName).toString())
    }

    fun routesPathForUser(userName: String) =
            File(context.filesDir.toString(), "/users/$userName/routes")

    private fun generatePath(user: User): File {

        val number = AnotherBikeApp.currentUser.savedRouteData.size + 1

        return File(
                user.routesPath,
                ROUTE_FILE_PREFIX + number.toString() + ".json"
        )
    }
}