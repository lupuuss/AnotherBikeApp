package ga.lupuss.anotherbikeapp.ui.modules.summary

import ga.lupuss.anotherbikeapp.Text
import ga.lupuss.anotherbikeapp.models.dataclass.ExtendedRouteData
import ga.lupuss.anotherbikeapp.models.base.PreferencesInteractor
import ga.lupuss.anotherbikeapp.models.base.RoutesManager
import ga.lupuss.anotherbikeapp.models.base.StringsResolver

class AfterTrackingSummaryPresenter(
        override var summaryView: SummaryView,
        override var routesManager: RoutesManager,
        override var stringsResolver: StringsResolver,
        override var preferencesInteractor: PreferencesInteractor
) : SummaryPresenter() {

    private lateinit var routeData: ExtendedRouteData

    override fun notifyOnViewReady() {

        summaryView.getRouteNameFromEditText()

        routesManager.getTempRoute() ?: throw IllegalStateException("no route to show")

        routeData = routesManager.getTempRoute()!!

        showExtendedRouteData(routeData)
    }

    override fun onSaveClick() {

        val name = summaryView.getRouteNameFromEditText()

        routeData.name = if (name != "") name else stringsResolver.resolve(Text.DEFAULT_ROUTE_NAME)

        routesManager.saveRoute(routeData)
        summaryView.finishActivity()
    }

    override fun onExitRequest() {

        onRejectClick()
    }

    override fun onRejectClick() {

        summaryView.showRejectDialog {

            summaryView.finishActivity()
        }
    }
}