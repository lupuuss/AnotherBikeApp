package ga.lupuss.anotherbikeapp.ui.modules.summary

import android.content.Context
import ga.lupuss.anotherbikeapp.R
import ga.lupuss.anotherbikeapp.base.Presenter
import ga.lupuss.anotherbikeapp.models.pojo.ExtendedRouteData
import ga.lupuss.anotherbikeapp.models.routes.RoutesManager
import ga.lupuss.anotherbikeapp.models.trackingservice.statisticsmanager.statistics.Statistic
import javax.inject.Inject

class SummaryPresenter @Inject constructor(private val routesManager: RoutesManager) : Presenter {

    @Inject
    lateinit var context: Context

    @Inject
    lateinit var summaryView: SummaryView

    private lateinit var routeData: ExtendedRouteData

    fun viewReady() {

        routesManager.routeKeeper.getRoute() ?: throw IllegalStateException("no route to show")

        routeData = routesManager.routeKeeper.getRoute()!!

        summaryView.showRouteLine(routeData.points)
        summaryView.showStatistics(routeData.getStatisticsMap(Statistic.Unit.KM_H, Statistic.Unit.KM))
    }

    fun onMapClick() {

    }

    fun onSaveClick() {

        val name = summaryView.getRouteNameFromEditText()

        routeData.name = if (name != "") name else context.getString(R.string.default_route_name)

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

            routesManager.routeKeeper.clear()
        }
    }
}
