package ga.lupuss.anotherbikeapp.ui.modules.routephotos

import ga.lupuss.anotherbikeapp.base.BaseView
import ga.lupuss.anotherbikeapp.base.Presenter
import ga.lupuss.anotherbikeapp.models.base.PathsGenerator
import ga.lupuss.anotherbikeapp.models.dataclass.RoutePhoto
import javax.inject.Inject

class RoutePhotosPresenter @Inject constructor(
        view: RoutePhotosView,
        private val pathsGenerator: PathsGenerator,
        private val timeProvider: () -> Long
) : Presenter<RoutePhotosView>() {

    init {
        this.view = view
    }

    fun onClickTakePhotoButton() {

        view.requestPhoto(BaseView.PhotoRequest(pathsGenerator.createNewPhotoFile()) { file, ok ->

            if(ok) {

                view.displayNewPhotoDialog(file) {

                    view.notifyPhotoTaken(RoutePhoto(file.absolutePath, it, timeProvider.invoke()))
                }
            }
        })
    }

}