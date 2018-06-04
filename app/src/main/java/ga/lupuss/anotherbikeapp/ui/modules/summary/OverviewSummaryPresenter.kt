package ga.lupuss.anotherbikeapp.ui.modules.summary

import ga.lupuss.anotherbikeapp.Text
import ga.lupuss.anotherbikeapp.models.base.PreferencesInteractor
import ga.lupuss.anotherbikeapp.models.base.RouteReference
import ga.lupuss.anotherbikeapp.models.base.RoutesManager
import ga.lupuss.anotherbikeapp.models.base.StringsResolver
import timber.log.Timber

class OverviewSummaryPresenter(
        override var summaryView: SummaryView,
        override var routesManager: RoutesManager,
        override var stringsResolver: StringsResolver,
        override var preferencesInteractor: PreferencesInteractor) : SummaryPresenter() {

    private lateinit var routeReference: RouteReference
    private lateinit var name: String

    fun passRouteReference(routeReference: RouteReference) {

        this.routeReference = routeReference
    }

    override fun notifyOnViewReady() {

        summaryView.isRouteEditLineVisible = false
        summaryView.isProgressBarVisible = true
        summaryView.isStatsFragmentVisible = false
        summaryView.isSaveActionVisible = false
        summaryView.isRejectActionVisible = false

        routesManager.requestExtendedRoutesData(
                routeReference = routeReference,
                onDataOk = {

                    summaryView.isRouteEditLineVisible = true
                    summaryView.isProgressBarVisible = false
                    summaryView.isStatsFragmentVisible = true
                    summaryView.isRejectActionVisible = true
                    name = it.name ?: stringsResolver.resolve(Text.DEFAULT_ROUTE_NAME)
                    showExtendedRouteData(it)
                },
                onDataFail = {
                    Timber.d(it)
                }
        )
    }

    override fun onMapClick() {

    }

    override fun onSaveClick() {

    }

    override fun onExitRequest() {

        if (name == summaryView.getRouteNameFromEditText()){

            summaryView.finishActivity()

        } else {

        }
    }

    override fun onRejectClick() {

        summaryView.showDeleteDialog {

            routesManager.deleteRoute(routeReference)
            summaryView.finishActivity()
        }
    }
}