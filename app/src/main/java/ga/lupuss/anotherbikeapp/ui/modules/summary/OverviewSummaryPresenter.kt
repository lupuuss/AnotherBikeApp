package ga.lupuss.anotherbikeapp.ui.modules.summary

import ga.lupuss.anotherbikeapp.Text
import ga.lupuss.anotherbikeapp.models.base.PreferencesInteractor
import ga.lupuss.anotherbikeapp.models.base.RouteReference
import ga.lupuss.anotherbikeapp.models.base.RoutesManager
import ga.lupuss.anotherbikeapp.models.base.ResourceResolver
import ga.lupuss.anotherbikeapp.models.dataclass.ExtendedRouteData

class OverviewSummaryPresenter(
        summaryView: SummaryView,
        override val routesManager: RoutesManager,
        override val resourceResolver: ResourceResolver,
        override val preferencesInteractor: PreferencesInteractor

) : SummaryPresenter(summaryView), RoutesManager.OnRequestExtendedRouteDataListener  {

    // Tests checks this field by reflection.
    // If you renames this field, you should rename it in tests as well.
    private lateinit var routeReference: RouteReference
    private lateinit var name: String

    fun passRouteReference(routeReference: RouteReference) {

        this.routeReference = routeReference
    }

    override fun notifyOnViewReady() {

        view.isRouteEditLineVisible = false
        view.isProgressBarVisible = true
        view.isStatsFragmentVisible = false
        view.isSaveActionVisible = false
        view.isRejectActionVisible = false

        val routeData = routesManager.getTempRoute(RoutesManager.Slot.SUMMARY_BACKUP)

        if (routeData != null) {

            onDataOk(routeData)

        } else {

            routesManager.requestExtendedRoutesData(

                    routeReference,
                    this,
                    view
            )
        }
    }

    override fun onDataOk(routeData: ExtendedRouteData) {
            view.isRouteEditLineVisible = true
            view.isProgressBarVisible = false
            view.isStatsFragmentVisible = true
            view.isRejectActionVisible = true
            name = routeData.name ?: resourceResolver.resolve(Text.DEFAULT_ROUTE_NAME)
            showExtendedRouteData(routeData)
        }

    override fun onMissingData() {

            view.isProgressBarVisible = false
            view.isStatsFragmentVisible = true
    }

    override fun onSaveClick() {

        val name = view.getRouteNameFromEditText()
        routesManager.changeName(routeReference, name)
        this.name = name
        view.isSaveActionVisible = false
    }

    override fun onExitRequest() {

        if (!::name.isInitialized || name == view.getRouteNameFromEditText()){

            view.finishActivity()

        } else {

            view.showUnsavedStateDialog {

                view.finishActivity()
            }
        }
    }

    override fun onRejectClick() {

        view.showDeleteDialog {

            routesManager.deleteRoute(routeReference)
            view.finishActivity()
        }
    }

    override fun onNameEditTextChanged(text: CharSequence?) {

        if (::name.isInitialized) {

            view.isSaveActionVisible = text.toString() != name
        }
    }

    override fun notifyOnDestroy(isFinishing: Boolean) {
        super.notifyOnDestroy(isFinishing)

        if (isFinishing) {

            routesManager.clearTempRoute(RoutesManager.Slot.SUMMARY_BACKUP)
        }
    }
}