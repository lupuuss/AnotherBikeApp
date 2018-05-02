package ga.lupuss.anotherbikeapp.models

import android.content.Context
import android.os.FileObserver
import android.os.Handler
import ga.lupuss.anotherbikeapp.ROUTE_FILE_PREFIX
import ga.lupuss.anotherbikeapp.models.pojo.SerializableRouteData
import ga.lupuss.anotherbikeapp.models.pojo.User
import timber.log.Timber
import java.io.File

/** Manages routes on phone memory. Saves routes to /unsync dir.
 * Reads routes form /sync and /unsync dir **/

class RoutesManager(private val filesManager: FilesManager,
                    private val user: User,
                    fileObserverFactory: FileObserverFactory,
                    context: Context) {

    private val fileObserver: FileObserver
    private val onRoutesChangedListeners = mutableListOf<() -> Unit>()

    init {
        refreshSavedRoutesList()

        fileObserver = fileObserverFactory.create(
                user.unsyncRoutesPath.absolutePath, // dir to observe path
                (FileObserver.CREATE
                        or FileObserver.DELETE
                        or FileObserver.CLOSE_WRITE
                        or FileObserver.DELETE_SELF), // mask
                { code, filePath ->
                    // onEvent

                    when (code) {
                        FileObserver.CREATE, FileObserver.DELETE, FileObserver.DELETE_SELF -> {
                            refreshSavedRoutesList()
                            Timber.d("Routes changed >>> Code: $code Changed file: $filePath")

                            Handler(context.mainLooper).post {

                                onRoutesChangedListeners.forEach {
                                    it.invoke()
                                }
                            }
                        }
                    }

                })
        fileObserver.startWatching()
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

    fun addOnRoutesChangedListener(onRoutesChanged: () -> Unit) {
        onRoutesChangedListeners.add(onRoutesChanged)
    }

    fun removeOnRoutesChangedListener(onRoutesChanged: () -> Unit) {

        onRoutesChangedListeners.remove(onRoutesChanged)
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