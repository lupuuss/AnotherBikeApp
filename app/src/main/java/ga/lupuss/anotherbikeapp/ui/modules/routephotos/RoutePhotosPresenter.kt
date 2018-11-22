package ga.lupuss.anotherbikeapp.ui.modules.routephotos

import ga.lupuss.anotherbikeapp.base.BaseView
import ga.lupuss.anotherbikeapp.base.Presenter
import ga.lupuss.anotherbikeapp.models.base.PathsGenerator
import javax.inject.Inject

class RoutePhotosPresenter @Inject constructor(
        view: RoutePhotosView,
        private val pathsGenerator: PathsGenerator
) : Presenter<RoutePhotosView>() {

    init {
        this.view = view
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