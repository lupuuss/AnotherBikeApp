package ga.lupuss.anotherbikeapp.ui.modules.summary

import ga.lupuss.anotherbikeapp.Text
import ga.lupuss.anotherbikeapp.models.dataclass.ExtendedRouteData
import ga.lupuss.anotherbikeapp.models.base.PreferencesInteractor
import ga.lupuss.anotherbikeapp.models.base.RoutesManager
import ga.lupuss.anotherbikeapp.models.base.StringsResolver
import timber.log.Timber

class AfterTrackingSummaryPresenter(
        summaryView: SummaryView,
        override val routesManager: RoutesManager,
        override val stringsResolver: StringsResolver,
        override val preferencesInteractor: PreferencesInteractor

) : SummaryPresenter(summaryView) {

    // Tests checks this field by reflection.
    // If you renames this field, you should rename it in tests as well.
    private lateinit var routeData: ExtendedRouteData

    override fun notifyOnViewReady() {

        if (routesManager.getTempRoute() == null) {

            view.finishActivity()
            Timber.w("no route to show")

        } else {

            routeData = routesManager.getTempRoute()!!
            showExtendedRouteData(routeData)
        }
    }

    override fun onSaveClick() {

        val name = view.getRouteNameFromEditText()

        val mutable = routeData.toMutable()

        mutable.name = if (name != "") name else stringsResolver.resolve(Text.DEFAULT_ROUTE_NAME)

        routesManager.saveRoute(mutable)
        view.finishActivity()
    }

    override fun onExitRequest() {

        onRejectClick()
    }

    override fun onRejectClick() {

        view.showRejectDialog {

            view.finishActivity()
        }
    }

    override fun onNameEditTextChanged(text: CharSequence?) {}
}