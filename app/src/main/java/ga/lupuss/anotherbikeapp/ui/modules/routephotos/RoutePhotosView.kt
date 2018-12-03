package ga.lupuss.anotherbikeapp.ui.modules.routephotos

import ga.lupuss.anotherbikeapp.base.BaseView
import ga.lupuss.anotherbikeapp.models.dataclass.Photo
import java.io.File

interface RoutePhotosView : BaseView {

    fun displayNewPhotoDialog(photoPath: File, onYesAction: (String) -> Unit)
    fun notifyPhotoTaken(photo: Photo)
}