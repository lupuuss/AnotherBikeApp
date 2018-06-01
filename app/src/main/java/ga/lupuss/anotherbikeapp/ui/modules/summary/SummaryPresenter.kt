package ga.lupuss.anotherbikeapp.ui.modules.summary

import ga.lupuss.anotherbikeapp.Text
import ga.lupuss.anotherbikeapp.base.Presenter
import ga.lupuss.anotherbikeapp.models.interfaces.PreferencesInteractor
import ga.lupuss.anotherbikeapp.models.dataclass.ExtendedRouteData
import ga.lupuss.anotherbikeapp.models.interfaces.RoutesManager
import ga.lupuss.anotherbikeapp.models.interfaces.StringsResolver
import timber.log.Timber
import javax.inject.Inject

class SummaryPresenter @Inject constructor(private val routesManager: RoutesManager,
                                           private val stringsResolver: StringsResolver,
                                           private val preferencesInteractor: PreferencesInteractor) : Presenter {

    @Inject
    lateinit var summaryView: SummaryView

    private lateinit var routeData: ExtendedRouteData

    private lateinit var mode: Mode
    private var docReference: String? = null

    enum class Mode {
        OVERVIEW, AFTER_TRACKING_SUMMARY
    }

    fun initMode(mode: Mode, docReference: String?) {
        this.mode = mode

        if (mode == Mode.OVERVIEW) {

            summaryView.isRouteEditLineVisible = false
            summaryView.isProgressBarVisible = true
            summaryView.isStatsFragmentVisible = false
            this.docReference = docReference
        }
    }

    override fun notifyOnViewReady() {

        if (mode == Mode.AFTER_TRACKING_SUMMARY) {

            routesManager.getTempRoute() ?: throw IllegalStateException("no route to show")

            routeData = routesManager.getTempRoute()!!

            showExtendedRouteData(routeData)

        } else {

            routesManager.requestExtendedRoutesData(
                    docReference!!,
                    {

                        summaryView.isStatsFragmentVisible = true
                        summaryView.isProgressBarVisible = false
                        summaryView.isRouteEditLineVisible = true
                        showExtendedRouteData(it)
                    },
                    { Timber.d(it) }
            )
        }
    }

    private fun showExtendedRouteData(routeData: ExtendedRouteData) {

        summaryView.showRouteLine(routeData.points)
        summaryView.showStatistics(
                routeData.getStatisticsMap(
                        preferencesInteractor.speedUnit,
                        preferencesInteractor.distanceUnit
                )
        )
    }

    fun onMapClick() {

    }

    fun onSaveClick() {

        if (mode == Mode.AFTER_TRACKING_SUMMARY) {

            val name = summaryView.getRouteNameFromEditText()

            routeData.name = if (name != "") name else stringsResolver.resolve(Text.DEFAULT_ROUTE_NAME)

            routesManager.saveRoute(routeData)
            summaryView.finishActivity()
        }
    }

    fun onRejectClick() {

        if (mode == Mode.AFTER_TRACKING_SUMMARY) {

            summaryView.showRejectDialog {

                summaryView.finishActivity()
            }
        } else {
            summaryView.finishActivity()
        }
    }

    override fun notifyOnDestroy(isFinishing: Boolean) {
        super.notifyOnDestroy(isFinishing)

        if (isFinishing) {

            routesManager.clearTempRoute()
        }
    }
}
