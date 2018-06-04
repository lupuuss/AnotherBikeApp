package ga.lupuss.anotherbikeapp.ui.modules.summary

import ga.lupuss.anotherbikeapp.models.base.PreferencesInteractor
import ga.lupuss.anotherbikeapp.models.base.RoutesManager
import ga.lupuss.anotherbikeapp.models.base.StringsResolver
import javax.inject.Inject


class MainSummaryPresenter @Inject constructor(): SummaryPresenter() {

    @Inject
    override lateinit var summaryView: SummaryView

    @Inject
    override lateinit var routesManager: RoutesManager

    @Inject
    override lateinit var stringsResolver: StringsResolver

    @Inject
    override lateinit var preferencesInteractor: PreferencesInteractor

    private lateinit var summaryPresenter: SummaryPresenter

    fun initMode(mode: Mode, routeReference: String?) {

        val summaryPresenter = when (mode) {

            SummaryPresenter.Mode.AFTER_TRACKING_SUMMARY->
                AfterTrackingSummaryPresenter(summaryView, routesManager, stringsResolver, preferencesInteractor)

            SummaryPresenter.Mode.OVERVIEW->
                OverviewSummaryPresenter(summaryView, routesManager, stringsResolver, preferencesInteractor)
        }

        if (summaryPresenter is OverviewSummaryPresenter) {

            summaryPresenter.passRouteReference(
                    routesManager.routeReferenceSerializer.deserialize(routeReference!!)
            )
        }

        this.summaryPresenter = summaryPresenter
    }

    override fun notifyOnViewReady() {

        summaryPresenter.notifyOnViewReady()
    }

    override fun onSaveClick() {

        summaryPresenter.onSaveClick()
    }

    override fun onExitRequest() {

        summaryPresenter.onExitRequest()
    }

    override fun onRejectClick() {

        summaryPresenter.onRejectClick()
    }

    override fun notifyOnDestroy(isFinishing: Boolean) {
        super.notifyOnDestroy(isFinishing)

        if (isFinishing) {

            routesManager.clearTempRoute()
        }
    }
}
