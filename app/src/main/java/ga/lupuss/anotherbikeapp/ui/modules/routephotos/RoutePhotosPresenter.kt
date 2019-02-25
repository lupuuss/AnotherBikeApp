package ga.lupuss.anotherbikeapp.ui.modules.routephotos

import ga.lupuss.anotherbikeapp.base.BaseView
import ga.lupuss.anotherbikeapp.base.Presenter
import ga.lupuss.anotherbikeapp.models.base.AuthInteractor
import ga.lupuss.anotherbikeapp.models.base.PathsGenerator
import ga.lupuss.anotherbikeapp.models.dataclass.RoutePhoto
import ga.lupuss.anotherbikeapp.sha256ofFile
import timber.log.Timber
import javax.inject.Inject

class RoutePhotosPresenter @Inject constructor(
        view: RoutePhotosView,
        private val pathsGenerator: PathsGenerator,
        private val timeProvider: () -> Long,
        private val authInteractor: AuthInteractor
) : Presenter<RoutePhotosView>() {

    init {
        this.view = view
    }

    fun onClickTakePhotoButton() {

        val userUid = authInteractor.userUid
        val time = timeProvider()
        val tempLink = "$userUid/$time.png"

        view.requestPhoto(BaseView.PhotoRequest(pathsGenerator.getPathForPhoto(tempLink)) { file, ok ->

            if(ok) {

                val sha256 = sha256ofFile(file)
                val link = "$userUid/$sha256.png"

                Timber.d("SHA 1 $sha256")

                val fileAfterRename = pathsGenerator.getPathForPhoto(link)
                file.renameTo(fileAfterRename)

                Timber.d(sha256ofFile(fileAfterRename))

                view.displayNewPhotoDialog(fileAfterRename) {

                     val name: String? = if (it.isEmpty()) null
                                            else it

                    view.notifyPhotoTaken(RoutePhoto(link, name, timeProvider.invoke()))
                }
            }
        })
    }

}