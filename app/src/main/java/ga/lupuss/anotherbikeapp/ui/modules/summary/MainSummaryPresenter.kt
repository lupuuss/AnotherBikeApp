package ga.lupuss.anotherbikeapp.ui.modules.summary

import ga.lupuss.anotherbikeapp.models.base.PreferencesInteractor
import ga.lupuss.anotherbikeapp.models.base.RoutesManager
import ga.lupuss.anotherbikeapp.models.base.ResourceResolver
import ga.lupuss.anotherbikeapp.models.dataclass.RoutePhoto

class MainSummaryPresenter(
        summaryView: SummaryView,
        override val routesManager: RoutesManager,
        override val resourceResolver: ResourceResolver,
        override val preferencesInteractor: PreferencesInteractor

): SummaryPresenter(summaryView) {


    // Tests checks this field by reflection.
    // If you renames this field, you should rename it in tests as well.
    private lateinit var subPresenter: SummaryPresenter

    fun initMode(mode: Mode, routeReference: String?) {

        val summaryPresenter = when (mode) {

            SummaryPresenter.Mode.AFTER_TRACKING_SUMMARY->
                AfterTrackingSummaryPresenter(view, routesManager, resourceResolver, preferencesInteractor)

            SummaryPresenter.Mode.OVERVIEW->
                OverviewSummaryPresenter(view, routesManager, resourceResolver, preferencesInteractor)
        }

        if (summaryPresenter is OverviewSummaryPresenter) {

            summaryPresenter.passRouteReference(
                    routesManager.routeReferenceSerializer.deserialize(routeReference!!)
            )
        }

        this.subPresenter = summaryPresenter
    }

    override fun notifyOnViewReady() {

        subPresenter.notifyOnViewReady()
    }

    override fun onSaveClick() {

        subPresenter.onSaveClick()
    }

    override fun onExitRequest() {

        subPresenter.onExitRequest()
    }

    override fun onRejectClick() {

        subPresenter.onRejectClick()
    }

    override fun onNameEditTextChanged(text: CharSequence?) {

        subPresenter.onNameEditTextChanged(text)
    }

    override fun notifyOnDestroy(isFinishing: Boolean) {
        super.notifyOnDestroy(isFinishing)

        subPresenter.notifyOnDestroy(isFinishing)
    }
}
