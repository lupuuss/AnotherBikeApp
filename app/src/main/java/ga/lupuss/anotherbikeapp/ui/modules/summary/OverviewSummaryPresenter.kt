package ga.lupuss.anotherbikeapp.ui.modules.summary

import ga.lupuss.anotherbikeapp.Text
import ga.lupuss.anotherbikeapp.models.base.PreferencesInteractor
import ga.lupuss.anotherbikeapp.models.base.RouteReference
import ga.lupuss.anotherbikeapp.models.base.RoutesManager
import ga.lupuss.anotherbikeapp.models.base.StringsResolver
import ga.lupuss.anotherbikeapp.models.dataclass.ExtendedRouteData
import timber.log.Timber

class OverviewSummaryPresenter(
        override val summaryView: SummaryView,
        override val routesManager: RoutesManager,
        override val stringsResolver: StringsResolver,
        override val preferencesInteractor: PreferencesInteractor

) : SummaryPresenter(), RoutesManager.OnRequestExtendedRouteDataListener  {

    // Tests checks this field by reflection.
    // If you renames this field, you should rename it in tests as well.
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

                routeReference,
                this
        )
    }

    override fun onDataOk(routeData: ExtendedRouteData) {
            summaryView.isRouteEditLineVisible = true
            summaryView.isProgressBarVisible = false
            summaryView.isStatsFragmentVisible = true
            summaryView.isRejectActionVisible = true
            name = routeData.name ?: stringsResolver.resolve(Text.DEFAULT_ROUTE_NAME)
            showExtendedRouteData(routeData)
        }

    override fun onMissingData() {

            summaryView.isProgressBarVisible = false
            summaryView.isStatsFragmentVisible = true
    }

    override fun onSaveClick() {

        val name = summaryView.getRouteNameFromEditText()
        routesManager.changeName(routeReference, name)
        this.name = name
        summaryView.isSaveActionVisible = false
    }

    override fun onExitRequest() {

        if (!::name.isInitialized || name == summaryView.getRouteNameFromEditText()){

            summaryView.finishActivity()

        } else {

            summaryView.showUnsavedStateDialog {

                summaryView.finishActivity()
            }
        }
    }

    override fun onRejectClick() {

        summaryView.showDeleteDialog {

            routesManager.deleteRoute(routeReference)
            summaryView.finishActivity()
        }
    }

    override fun onNameEditTextChanged(text: CharSequence?) {

        if (::name.isInitialized) {

            Timber.d("${text?.toString()}|")
            Timber.d("$name|")
            summaryView.isSaveActionVisible = text.toString() != name
        }
    }
}