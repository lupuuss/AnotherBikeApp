package ga.lupuss.anotherbikeapp.ui.modules.summary

import ga.lupuss.anotherbikeapp.base.Presenter
import ga.lupuss.anotherbikeapp.models.dataclass.ExtendedRouteData
import ga.lupuss.anotherbikeapp.models.base.PreferencesInteractor
import ga.lupuss.anotherbikeapp.models.base.RoutesManager
import ga.lupuss.anotherbikeapp.models.base.StringsResolver

abstract class SummaryPresenter  : Presenter {


    abstract var summaryView: SummaryView


    abstract var routesManager: RoutesManager


    abstract var stringsResolver: StringsResolver


    abstract var preferencesInteractor: PreferencesInteractor


    enum class Mode {
        OVERVIEW, AFTER_TRACKING_SUMMARY
    }

    protected fun showExtendedRouteData(routeData: ExtendedRouteData) {

        summaryView.showRouteLine(routeData.points)
        summaryView.showStatistics(
                routeData.getStatisticsMap(
                        preferencesInteractor.speedUnit,
                        preferencesInteractor.distanceUnit
                )
        )

        summaryView.setNameLabelValue(routeData.name ?: "")
    }

    abstract fun onSaveClick()

    abstract fun onExitRequest()

    abstract fun onRejectClick()

}