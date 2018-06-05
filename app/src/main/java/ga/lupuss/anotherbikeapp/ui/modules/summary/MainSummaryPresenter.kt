package ga.lupuss.anotherbikeapp.ui.modules.summary

import ga.lupuss.anotherbikeapp.models.base.PreferencesInteractor
import ga.lupuss.anotherbikeapp.models.base.RoutesManager
import ga.lupuss.anotherbikeapp.models.base.StringsResolver
import javax.inject.Inject


class MainSummaryPresenter @Inject constructor(
        override val summaryView: SummaryView,
        override val routesManager: RoutesManager,
        override val stringsResolver: StringsResolver ,
        override val preferencesInteractor: PreferencesInteractor

): SummaryPresenter() {

    lateinit var subPresenter: SummaryPresenter
        private set

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

        if (isFinishing) {

            routesManager.clearTempRoute()
        }
    }
}
