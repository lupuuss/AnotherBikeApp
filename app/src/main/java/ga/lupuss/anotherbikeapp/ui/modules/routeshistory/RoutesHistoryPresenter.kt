package ga.lupuss.anotherbikeapp.ui.modules.routeshistory

import ga.lupuss.anotherbikeapp.base.Presenter
import ga.lupuss.anotherbikeapp.models.base.PreferencesInteractor
import ga.lupuss.anotherbikeapp.models.base.RoutesManager
import ga.lupuss.anotherbikeapp.models.dataclass.Statistic
import ga.lupuss.anotherbikeapp.models.firebase.OnDataSetChanged
import timber.log.Timber
import javax.inject.Inject

class RoutesHistoryPresenter @Inject constructor(
        routesHistoryView: RoutesHistoryView,
        private val preferencesInteractor: PreferencesInteractor,
        private val routesManager: RoutesManager
) : Presenter<RoutesHistoryView>(),
        PreferencesInteractor.OnUnitChangedListener,
        RoutesManager.OnRequestMoreShortRouteDataListener,
        OnDataSetChanged {

    init {
        view = routesHistoryView
    }

    var speedUnit: Statistic.Unit = preferencesInteractor.speedUnit
    var distanceUnit: Statistic.Unit = preferencesInteractor.distanceUnit

    private var loadMoreAvailable = true
    private var requestInProgress = false

    override fun notifyOnViewReady() {

        view.isNoDataTextVisible = false
        preferencesInteractor.addOnUnitChangedListener(this, this)
        routesManager.addRoutesDataChangedListener(this)
        onLoadMoreRequest()
    }

    private fun setLoading(isOn: Boolean) {
        view.isRoutesHistoryProgressBarVisible = isOn
        view.isRefreshProgressBarVisible = isOn
        view.isRefreshButtonVisible = !isOn
        view.isNoDataTextVisible = false
    }

    private fun onLoadMoreRequest() {

        if (loadMoreAvailable && !requestInProgress) {

            setLoading(true)
            requestInProgress = true
            routesManager.requestMoreShortRouteData(this, view)
        }
    }

    override fun notifyOnDestroy(isFinishing: Boolean) {

        routesManager.removeOnRoutesDataChangedListener(this)
        preferencesInteractor.removeOnUnitChangedListener(this)
        super.notifyOnDestroy(isFinishing)
    }

    fun notifyRecyclerReachedBottom() {

        onLoadMoreRequest()
    }

    override fun onRequestSuccess() {

        setLoading(false)
        requestInProgress = false
    }

    override fun onDataEnd() {

        loadMoreAvailable = false

        if (routesManager.shortRouteDataCount() == 0)
            view.isNoDataTextVisible = true

    }

    override fun onFail(exception: Exception) {

        setLoading(false)
        view.makeToast(exception.toString())
    }

    fun onHistoryRecyclerItemRequest(position: Int) = routesManager.readShortRouteData(position)

    fun onHistoryRecyclerItemCountRequest(): Int = routesManager.shortRouteDataCount()

    fun onClickRefreshButton() {

        setLoading(true)
        loadMoreAvailable = true
        routesManager.refresh(this, view)
    }

    fun onClickShortRoute(position: Int) {

        view.startSummaryActivity(
                routesManager.routeReferenceSerializer.serialize(
                        routesManager.getRouteReference(position)
                )
        )

    }

    override fun onNewDocument(position: Int) {

        view.isRefreshButtonVisible = true
        view.isRefreshProgressBarVisible = false

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

    override fun onDataSetChanged() {
        view.refreshRecyclerAdapter()
    }

    override fun onUnitChanged(speedUnit: Statistic.Unit.Speed, distanceUnit: Statistic.Unit.Distance) {

        this.speedUnit = speedUnit
        this.distanceUnit = distanceUnit

        view.refreshRecyclerAdapter()
    }
}