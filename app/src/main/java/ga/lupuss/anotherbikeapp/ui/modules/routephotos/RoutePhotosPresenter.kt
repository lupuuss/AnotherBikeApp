package ga.lupuss.anotherbikeapp.ui.modules.routephotos

import ga.lupuss.anotherbikeapp.base.BaseView
import ga.lupuss.anotherbikeapp.base.Presenter
import ga.lupuss.anotherbikeapp.models.base.PathsGenerator
import java.io.File

class RoutePhotosPresenter(
        view: RoutePhotosView,
        private val pathsGenerator: PathsGenerator
) : Presenter<RoutePhotosView>() {

    init {
        this.view = view
    }

    override fun notifyOnViewReady() {

        super.notifyOnViewReady()
    }

    fun onClickTakePhotoButton() {
        view.requestPhoto(BaseView.PhotoRequest(pathsGenerator.createNewPhotoFile()) { file, ok ->

            if(ok) {

                view.notifyPhotoTaken(file)

            } else {


            }
        })
    }

}