package ga.lupuss.anotherbikeapp.models.base

import ga.lupuss.anotherbikeapp.models.dataclass.RoutePhoto

interface PhotosSynchronizer {

    fun uploadAll(photos: List<RoutePhoto>)
    fun cancelAll()
    fun removeAll(photos: List<RoutePhoto>)
    fun removePhotoFile(photo: RoutePhoto)
    fun getDownloadUrl(link: String, onSuccess: (String) -> Unit, onFail: () -> Unit)
}
