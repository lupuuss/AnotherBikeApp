package ga.lupuss.anotherbikeapp.ui.modules.routeshistory

import ga.lupuss.anotherbikeapp.base.Presenter
import ga.lupuss.anotherbikeapp.models.base.PreferencesInteractor
import ga.lupuss.anotherbikeapp.models.base.RoutesManager
import ga.lupuss.anotherbikeapp.models.dataclass.Statistic
import ga.lupuss.anotherbikeapp.models.firebase.OnDocumentChanged
import timber.log.Timber
import javax.inject.Inject

class RoutesHistoryPresenter @Inject constructor(
        routesHistoryView: RoutesHistoryView,
        private val preferencesInteractor: PreferencesInteractor,
        private val routesManager: RoutesManager
) : Presenter<RoutesHistoryView>(),
        PreferencesInteractor.OnUnitChangedListener,
        RoutesManager.OnRequestMoreShortRouteDataListener,
        OnDocumentChanged {

    init {
        view = routesHistoryView
    }

    var speedUnit: Statistic.Unit = preferencesInteractor.speedUnit
    var distanceUnit: Statistic.Unit = preferencesInteractor.distanceUnit

    private var loadMoreAvailable = true

    override fun notifyOnViewReady() {

        view.isNoDataTextVisible = false
        preferencesInteractor.addOnUnitChangedListener(this, this)
        routesManager.addRoutesDataChangedListener(this)
        onLoadMoreRequest()
    }

    private fun onLoadMoreRequest() {

        if (loadMoreAvailable) {

            view.isProgressBarVisible = true

            routesManager.requestMoreShortRouteData(this, view)
        }
    }

    fun notifyRecyclerReachedBottom() {

        onLoadMoreRequest()
    }

    override fun notifyOnDestroy(isFinishing: Boolean) {

        routesManager.removeOnRoutesDataChangedListener(this)
        preferencesInteractor.removeOnUnitChangedListener(this)
        super.notifyOnDestroy(isFinishing)
    }


    fun onClickShortRoute(position: Int) {

        view.startSummaryActivity(
                routesManager.routeReferenceSerializer.serialize(
                        routesManager.getRouteReference(position)
                )
        )

    }

    override fun onDataEnd() {

        view.isProgressBarVisible = false

        loadMoreAvailable = false

        if (routesManager.shortRouteDataCount() == 0)
            view.isNoDataTextVisible = true

    }

    override fun onFail(exception: Exception) {

        view.isProgressBarVisible = false
        Timber.e(exception)
    }

    fun onHistoryRecyclerItemRequest(position: Int) = routesManager.readShortRouteData(position)

    fun onHistoryRecyclerItemCountRequest(): Int = routesManager.shortRouteDataCount()

    override fun onNewDocument(position: Int) {

        view.isNoDataTextVisible = false
        view.notifyRecyclerItemInserted(position, routesManager.shortRouteDataCount())
    }

    override fun onDocumentModified(position: Int) {
        view.notifyRecyclerItemChanged(position)
    }

    override fun onDocumentDeleted(position: Int) {
        view.notifyRecyclerItemRemoved(position, routesManager.shortRouteDataCount())

        if (routesManager.shortRouteDataCount() == 0) {

            view.isNoDataTextVisible = true
        }
    }

    override fun onUnitChanged(speedUnit: Statistic.Unit.Speed, distanceUnit: Statistic.Unit.Distance) {

        this.speedUnit = speedUnit
        this.distanceUnit = distanceUnit

        view.refreshRecyclerAdapter()
    }
}