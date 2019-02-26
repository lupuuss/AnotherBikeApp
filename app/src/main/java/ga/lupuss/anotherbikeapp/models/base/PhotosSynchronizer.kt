package ga.lupuss.anotherbikeapp.models.base

import ga.lupuss.anotherbikeapp.models.dataclass.RoutePhoto
import java.io.File

interface PhotosSynchronizer {

    fun uploadAll(photos: List<RoutePhoto>)
    fun cancelAll()
    fun removeAll(photos: List<RoutePhoto>)
    fun removePhotoFile(photo: RoutePhoto)
    fun getPathForPhotoLink(link: String): File
}
