package ga.lupuss.anotherbikeapp.ui.modules.summary

import ga.lupuss.anotherbikeapp.base.Presenter
import ga.lupuss.anotherbikeapp.models.RoutesManager
import ga.lupuss.anotherbikeapp.models.pojo.SerializableRouteData
import ga.lupuss.anotherbikeapp.models.trackingservice.statisticsmanager.statistics.Statistic
import javax.inject.Inject

class SummaryPresenter @Inject constructor(private val routesManager: RoutesManager) : Presenter {

    @Inject
    lateinit var summaryView: SummaryView

    private lateinit var routeData: SerializableRouteData

    fun viewReady() {

        routesManager.temporaryRoute ?: throw IllegalStateException("No route to show")

        routeData = routesManager.temporaryRoute!!

        summaryView.showRouteLine(routeData.savedRoute)
        summaryView.showStatistics(routeData.getStatisticsMap(Statistic.Unit.KM_H, Statistic.Unit.KM))
    }

    fun onMapClick() {

    }

    fun onSaveClick() {

        val name = summaryView.getRouteNameFromEditText()

        if (name != "") {
            routeData.name = name
        }
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

            routesManager.clearTemporaryRoute()
        }
    }
}
