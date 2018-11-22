package ga.lupuss.anotherbikeapp.ui.modules.routephotos

import ga.lupuss.anotherbikeapp.base.BaseView
import java.io.File

interface RoutePhotosView : BaseView {

    fun notifyPhotoTaken(file: File)
}