package ga.lupuss.anotherbikeapp.ui.modules.summary

import ga.lupuss.anotherbikeapp.Text
import ga.lupuss.anotherbikeapp.base.Presenter
import ga.lupuss.anotherbikeapp.models.pojo.ExtendedRouteData
import ga.lupuss.anotherbikeapp.models.interfaces.RoutesManager
import ga.lupuss.anotherbikeapp.models.interfaces.StringsResolver
import ga.lupuss.anotherbikeapp.models.trackingservice.statisticsmanager.statistics.Statistic
import javax.inject.Inject

class SummaryPresenter @Inject constructor(private val routesManager: RoutesManager,
                                           private val stringsResolver: StringsResolver) : Presenter {

    @Inject
    lateinit var summaryView: SummaryView

    private lateinit var routeData: ExtendedRouteData

    override fun notifyOnViewReady() {

        routesManager.getTempRoute() ?: throw IllegalStateException("no route to show")

        routeData = routesManager.getTempRoute()!!

        summaryView.showRouteLine(routeData.points)
        summaryView.showStatistics(routeData.getStatisticsMap(Statistic.Unit.KM_H, Statistic.Unit.KM))
    }

    fun onMapClick() {

    }

    fun onSaveClick() {

        val name = summaryView.getRouteNameFromEditText()

        routeData.name = if (name != "") name else stringsResolver.resolve(Text.DEFAULT_ROUTE_NAME)

        routesManager.saveRoute(routeData)
        summaryView.finishActivity()
    }

    fun onRejectClick() {

        summaryView.showRejectDialog {

            summaryView.finishActivity()
        }
    }

    override fun notifyOnDestroy(isFinishing: Boolean) {
        super.notifyOnDestroy(isFinishing)

        if (isFinishing) {

            routesManager.clearTempRoute()
        }
    }
}
