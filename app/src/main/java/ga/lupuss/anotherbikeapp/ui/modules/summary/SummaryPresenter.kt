package ga.lupuss.anotherbikeapp.ui.modules.summary

import ga.lupuss.anotherbikeapp.AnotherBikeApp
import ga.lupuss.anotherbikeapp.base.Presenter
import ga.lupuss.anotherbikeapp.models.FilesManager
import ga.lupuss.anotherbikeapp.models.RoutesKeeper
import ga.lupuss.anotherbikeapp.models.pojo.SerializableRouteData
import ga.lupuss.anotherbikeapp.models.trackingservice.statisticsmanager.statistics.Statistic
import javax.inject.Inject

class SummaryPresenter @Inject constructor() : Presenter {

    @Inject
    lateinit var summaryView: SummaryView

    @Inject
    lateinit var routesKeeper: RoutesKeeper

    private lateinit var routeData: SerializableRouteData

    fun onFilePathPass(path: String) {

        routeData = routesKeeper.readRoute(path)

        summaryView.showRouteLine(routeData.savedRoute)
        summaryView.showStatistics(routeData.getStatisticsMap(Statistic.Unit.KM_H, Statistic.Unit.KM))
    }

    fun onMapClick() {

    }

    fun onSaveClick() {
        routesKeeper.saveRoute(AnotherBikeApp.currentUser, routeData)
        summaryView.finishActivity()
    }

    fun onRejectClick() {

        summaryView.showRejectDialog {

            summaryView.finishActivity()
        }
    }
}
