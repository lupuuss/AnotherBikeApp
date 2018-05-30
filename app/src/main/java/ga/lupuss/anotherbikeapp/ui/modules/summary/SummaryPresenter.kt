package ga.lupuss.anotherbikeapp.ui.modules.summary

import android.content.Context
import ga.lupuss.anotherbikeapp.R
import ga.lupuss.anotherbikeapp.base.Presenter
import ga.lupuss.anotherbikeapp.models.pojo.ExtendedRouteData
import ga.lupuss.anotherbikeapp.models.routes.FirebaseRoutesManager
import ga.lupuss.anotherbikeapp.models.routes.RoutesManager
import ga.lupuss.anotherbikeapp.models.trackingservice.statisticsmanager.statistics.Statistic
import javax.inject.Inject

class SummaryPresenter @Inject constructor(routesManager: FirebaseRoutesManager) : Presenter {

    @Inject
    lateinit var context: Context

    @Inject
    lateinit var summaryView: SummaryView

    private lateinit var routeData: ExtendedRouteData

    private val routesManager: RoutesManager = routesManager

    fun viewReady() {

        routesManager.getTempRoute() ?: throw IllegalStateException("no route to show")

        routeData = routesManager.getTempRoute()!!

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

            routesManager.clearTempRoute()
        }
    }
}
