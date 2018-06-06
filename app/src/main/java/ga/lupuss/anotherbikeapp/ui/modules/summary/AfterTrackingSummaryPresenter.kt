package ga.lupuss.anotherbikeapp.ui.modules.summary

import ga.lupuss.anotherbikeapp.Text
import ga.lupuss.anotherbikeapp.models.dataclass.ExtendedRouteData
import ga.lupuss.anotherbikeapp.models.base.PreferencesInteractor
import ga.lupuss.anotherbikeapp.models.base.RoutesManager
import ga.lupuss.anotherbikeapp.models.base.StringsResolver

class AfterTrackingSummaryPresenter(
        override val summaryView: SummaryView,
        override val routesManager: RoutesManager,
        override val stringsResolver: StringsResolver,
        override val preferencesInteractor: PreferencesInteractor

) : SummaryPresenter() {

    // Tests checks this field by reflection.
    // If you renames this field, you should rename it in tests as well.
    private lateinit var routeData: ExtendedRouteData

    override fun notifyOnViewReady() {

        routesManager.getTempRoute() ?: throw IllegalStateException("no route to show")

        routeData = routesManager.getTempRoute()!!

        showExtendedRouteData(routeData)
    }

    override fun onSaveClick() {

        val name = summaryView.getRouteNameFromEditText()

        routeData.name = if (name != "") name else stringsResolver.resolve(Text.DEFAULT_ROUTE_NAME)

        routesManager.saveRoute(routeData)
        summaryView.finishActivity()
    }

    override fun onExitRequest() {

        onRejectClick()
    }

    override fun onRejectClick() {

        summaryView.showRejectDialog {

            summaryView.finishActivity()
        }
    }

    override fun onNameEditTextChanged(text: CharSequence?) {}
}