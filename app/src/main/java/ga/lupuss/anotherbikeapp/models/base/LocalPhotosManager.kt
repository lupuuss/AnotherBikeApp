package ga.lupuss.anotherbikeapp.models.base

import ga.lupuss.anotherbikeapp.models.dataclass.RoutePhoto

interface LocalPhotosManager {

    val photosCount: Int

    fun addLocalPhoto(photo: RoutePhoto)
    fun removeLocalPhoto(position: Int)
    fun getLocalPhoto(position: Int): RoutePhoto
}