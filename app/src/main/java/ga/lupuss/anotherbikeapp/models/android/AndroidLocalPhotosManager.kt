package ga.lupuss.anotherbikeapp.models.android
import ga.lupuss.anotherbikeapp.models.base.LocalPhotosManager
import ga.lupuss.anotherbikeapp.models.dataclass.RoutePhoto

class AndroidLocalPhotosManager : LocalPhotosManager {

    private val photos = mutableListOf<RoutePhoto>()
    override val photosCount: Int
        get() =  photos.size

    override fun addLocalPhoto(photo: RoutePhoto) {

        photos.add(0, photo)
    }

    override fun getLocalPhoto(position: Int): RoutePhoto {
        return photos[position]
    }

    override fun removeLocalPhoto(position: Int) {
        photos.removeAt(position)
    }
}