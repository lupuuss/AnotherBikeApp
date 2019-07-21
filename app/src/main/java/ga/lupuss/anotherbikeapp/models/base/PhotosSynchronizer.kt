package ga.lupuss.anotherbikeapp.models.base

import ga.lupuss.anotherbikeapp.models.dataclass.MarkedRoutePhoto
import ga.lupuss.anotherbikeapp.models.dataclass.RoutePhoto
import java.io.File

interface PhotosSynchronizer {

    fun uploadAll(photos: List<MarkedRoutePhoto>)
    fun cancelAllUploads()
    fun removeFile(photo: RoutePhoto)
    fun rejectUpload(photo: MarkedRoutePhoto)
    fun rejectAllUploadsForRoute(routeId: String)
    fun getPathForPhotoLink(link: String): File
}
