package ga.lupuss.anotherbikeapp.ui.modules.routephotos

import ga.lupuss.anotherbikeapp.base.BaseView
import ga.lupuss.anotherbikeapp.base.Presenter
import ga.lupuss.anotherbikeapp.models.base.AuthInteractor
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

        val tempFile = pathsGenerator.getTempPhotoFile()

        view.requestPhoto(BaseView.PhotoRequest(tempFile) { file, ok ->

            if(!ok && file.exists()) {

                file.delete()
                return@PhotoRequest Unit

            } else if (!ok){

                return@PhotoRequest Unit
            }



            val fileAfterRename = pathsGenerator.getPhotoFileForTemp(tempFile)
            file.renameTo(fileAfterRename)

            view.displayNewPhotoDialog(fileAfterRename,

                    onYesAction = {

                        val name: String? = if (it.isEmpty()) null
                        else it

                        view.notifyPhotoTaken(
                                RoutePhoto(
                                        pathsGenerator.getLinkForFile(fileAfterRename),
                                        name,
                                        timeProvider()
                                )
                        )

                    },
                    onNoAction = {

                        listOf(tempFile, fileAfterRename).forEach {

                            if (it.exists()) {
                                it.delete()
                            }
                        }

                    })
        })
    }

}