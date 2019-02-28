package ga.lupuss.anotherbikeapp.ui.modules.summary

import ga.lupuss.anotherbikeapp.base.Presenter
import ga.lupuss.anotherbikeapp.models.base.*
import ga.lupuss.anotherbikeapp.models.dataclass.ExtendedRouteData

abstract class SummaryPresenter(summaryView: SummaryView)  : Presenter<SummaryView>() {

    init {
        view = summaryView
    }

    abstract val routesManager: RoutesManager
    abstract val resourceResolver: ResourceResolver
    abstract val preferencesInteractor: PreferencesInteractor
    abstract var routeData: ExtendedRouteData

    enum class Mode {
        OVERVIEW, AFTER_TRACKING_SUMMARY
    }

    protected fun showExtendedRouteData() {

        view.showRouteLine(routeData.points)
        view.showStatistics(
                routeData.getStatisticsMap(
                        preferencesInteractor.trackingSpeedUnit,
                        preferencesInteractor.trackingDistanceUnit
                )
        )

        view.setNameLabelValue(routeData.name ?: "")
        view.setPhotosAdaptersCallbacks(
                { position -> this.routeData.photos[position] },
                { this.routeData.photos.size }
        )
    }

    abstract fun onSaveClick()

    abstract fun onExitRequest()

    abstract fun onRejectClick()

    abstract fun onNameEditTextChanged(text: CharSequence?)

    abstract fun onClickDeletePhoto(position: Int)

    abstract fun onClickPhotoThumbnail(position: Int)

}