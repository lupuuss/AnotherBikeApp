package ga.lupuss.anotherbikeapp.models

import ga.lupuss.anotherbikeapp.ROUTE_FILE_PREFIX
import ga.lupuss.anotherbikeapp.models.pojo.SerializableRouteData
import ga.lupuss.anotherbikeapp.models.pojo.User
import java.io.File

/** Manages routes on phone memory. Saves routes to /unsync dir.
 * Reads routes form /sync and /unsync dir **/
class RoutesManager(private val filesManager: FilesManager,
                    private val user: User) {

    init {
        refreshSavedRoutesList()
    }

    var temporaryRoute: SerializableRouteData? = null

    fun saveRoute(routeData: SerializableRouteData) {

        refreshSavedRoutesList()
        filesManager.saveObjectToFile(routeData, generatePath(routeData).toString())
        refreshSavedRoutesList()
    }

    fun refreshSavedRoutesList() {

        if (user.isDefault) {
            user.savedRouteData =
                    sortedFilesByNameNumber(filesManager.makeChildrenListFor(user.syncRoutesPath))

        } else {
            val list: MutableList<File> = filesManager.makeChildrenListFor(user.syncRoutesPath)
                    .toMutableList()

            list.addAll(filesManager.makeChildrenListFor(user.unsyncRoutesPath))

            user.savedRouteData = sortedFilesByNameNumber(list)
        }
    }

    fun readRoute(position: Int): SerializableRouteData {

        return filesManager.readFileToObject(
                user.savedRouteData[position], SerializableRouteData::class.java)
    }

    fun routesCount(): Int {

        return user.savedRouteData.size
    }

    fun clearTemporaryRoute() {
        temporaryRoute = null
    }

    private fun generatePath(routeData: SerializableRouteData): File {

        return File(
                user.unsyncRoutesPath,
                ROUTE_FILE_PREFIX + routeData.startTime.toString() + ".json"
        )
    }

    private fun sortedFilesByNameNumber(list: List<File>) = list.sortedByDescending {
        it.name.substringAfter(ROUTE_FILE_PREFIX).substringBefore(".json").toLong()
    }
}