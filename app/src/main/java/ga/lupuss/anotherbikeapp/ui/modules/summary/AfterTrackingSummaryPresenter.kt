package ga.lupuss.anotherbikeapp.ui.modules.summary

import ga.lupuss.anotherbikeapp.Text
import ga.lupuss.anotherbikeapp.models.base.*
import ga.lupuss.anotherbikeapp.models.dataclass.ExtendedRouteData
import timber.log.Timber

class AfterTrackingSummaryPresenter(
        summaryView: SummaryView,
        override val routesManager: RoutesManager,
        override val resourceResolver: ResourceResolver,
        override val preferencesInteractor: PreferencesInteractor

) : SummaryPresenter(summaryView) {

    // Tests checks this field by reflection.
    // If you renames this field, you should rename it in tests as well.
    override lateinit var routeData: ExtendedRouteData

    override fun notifyOnViewReady() {

        if (routesManager.getTempRoute(RoutesManager.Slot.MAIN_TO_SUMMARY) == null) {

            view.finishActivity()
            Timber.e("=== Route passed to SummaryActivity is missing! ===")

        } else {

            routeData = routesManager.getTempRoute(RoutesManager.Slot.MAIN_TO_SUMMARY)!!
            showExtendedRouteData()
        }
    }

    override fun onSaveClick() {

        val name = view.getRouteNameFromEditText()

        val mutable = routeData.toMutable()

        mutable.name = if (name != "") name else resourceResolver.resolve(Text.DEFAULT_ROUTE_NAME)

        routesManager.saveRoute(mutable)
        view.finishActivity()
    }

    override fun onExitRequest() {

        onRejectClick()
    }

    override fun onRejectClick() {

        view.showRejectDialog {

            routeData.photos.forEach {

                routesManager.removePhotoFile(it)
            }

            view.finishActivity()
        }
    }

    override fun onNameEditTextChanged(text: CharSequence?) {}

    override fun onClickDeletePhoto(position: Int) {

        val mutable = routeData.toMutable()

        val route = mutable.photos[position]

        mutable.photos.removeAt(position)

        routesManager.removePhotoFile(route)

        routeData = mutable

        view.notifyPhotoDeleted(position, routeData.photos.size)
    }

    override fun onClickPhotoThumbnail(position: Int) {

        val list = routeData.photos.map {

            routesManager.getImageReference(it)
        }

        view.displayImage(list, list[position])
    }

    override fun notifyOnDestroy(isFinishing: Boolean) {
        super.notifyOnDestroy(isFinishing)

        if (isFinishing) {

            routesManager.clearTempRoute(RoutesManager.Slot.MAIN_TO_SUMMARY)
        }
    }
}