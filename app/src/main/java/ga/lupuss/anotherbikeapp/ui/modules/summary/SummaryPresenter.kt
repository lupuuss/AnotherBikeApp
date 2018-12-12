package ga.lupuss.anotherbikeapp.ui.modules.summary

import ga.lupuss.anotherbikeapp.base.Presenter
import ga.lupuss.anotherbikeapp.models.dataclass.ExtendedRouteData
import ga.lupuss.anotherbikeapp.models.base.PreferencesInteractor
import ga.lupuss.anotherbikeapp.models.base.RoutesManager
import ga.lupuss.anotherbikeapp.models.base.ResourceResolver

abstract class SummaryPresenter(summaryView: SummaryView)  : Presenter<SummaryView>() {

    init {
        view = summaryView
    }

    abstract val routesManager: RoutesManager
    abstract val resourceResolver: ResourceResolver
    abstract val preferencesInteractor: PreferencesInteractor

    enum class Mode {
        OVERVIEW, AFTER_TRACKING_SUMMARY
    }

    protected fun showExtendedRouteData(routeData: ExtendedRouteData) {

        view.showRouteLine(routeData.points)
        view.showStatistics(
                routeData.getStatisticsMap(
                        preferencesInteractor.trackingSpeedUnit,
                        preferencesInteractor.trackingDistanceUnit
                )
        )

        view.setNameLabelValue(routeData.name ?: "")
        view.setPhotosAdaptersCallbacks(
                { position -> routeData.photos[position] },
                { routeData.photos.size }
        )
    }

    abstract fun onSaveClick()

    abstract fun onExitRequest()

    abstract fun onRejectClick()

    abstract fun onNameEditTextChanged(text: CharSequence?)

}