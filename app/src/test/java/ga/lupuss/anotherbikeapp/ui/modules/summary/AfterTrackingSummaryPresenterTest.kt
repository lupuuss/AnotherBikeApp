package ga.lupuss.anotherbikeapp.ui.modules.summary

import com.nhaarman.mockito_kotlin.*
import ga.lupuss.anotherbikeapp.AppUnit
import ga.lupuss.anotherbikeapp.Text
import ga.lupuss.anotherbikeapp.models.base.PreferencesInteractor
import ga.lupuss.anotherbikeapp.models.base.RoutesManager
import ga.lupuss.anotherbikeapp.models.base.ResourceResolver
import ga.lupuss.anotherbikeapp.models.dataclass.ExtendedRouteData
import org.junit.Assert
import org.junit.Test

class AfterTrackingSummaryPresenterTest {

    private val summaryView: SummaryView = mock { }

    private val routeData: ExtendedRouteData = ExtendedRouteData.Instance(
            "name",
            0.0,
            0.0,
            0.0,
            0L,
            "",
            0L,
            0.0,
            0.0,
            0.0,
            emptyList(),
            emptyList()
    )

    private val resourceResolver: ResourceResolver = mock {
        on { resolve(Text.DEFAULT_ROUTE_NAME) }.then { "Route" }
    }

    private val preferencesInteractor: PreferencesInteractor = mock {
        on { trackingSpeedUnit }.then { AppUnit.Speed.KM_H }
        on { trackingDistanceUnit }.then { AppUnit.Distance.KM }
    }

    @Test
    fun notifyOnViewReady_whenAvailable_shouldShowRouteData() {

        val summaryPresenter = AfterTrackingSummaryPresenter(
                summaryView,
                mock { on { getTempRoute(any()) }.then { routeData } },
                resourceResolver,
                preferencesInteractor
        )

        summaryPresenter.notifyOnViewReady()

        verify(summaryView, times(1)).showRouteLine(routeData.points)
        verify(summaryView, times(1)).showStatistics(
                routeData.toMutable().getStatisticsMap(AppUnit.Speed.KM_H, AppUnit.Distance.KM)
        )
        verify(summaryView, times(1)).setNameLabelValue(routeData.name ?: "")

    }

    @Test
    fun notifyOnViewReady_whenRouteNotAvailable_shouldFinishActivity() {

        val summaryPresenter = AfterTrackingSummaryPresenter(
                summaryView,
                mock { on { getTempRoute(any()) }.then { null } },
                resourceResolver,
                preferencesInteractor
        )

        summaryPresenter.notifyOnViewReady()

        verify(summaryView, times(1)).finishActivity()
    }

    @Test
    fun onSaveClick_shouldSaveRouteAndFinishActivity() {

        val routesManager: RoutesManager = mock {
            on { getTempRoute(any()) }.then { routeData }
        }

        val summaryView = mock<SummaryView> { on { getRouteNameFromEditText() }.then { "" } }
        val summaryPresenter =  AfterTrackingSummaryPresenter(
                summaryView,
                routesManager,
                resourceResolver,
                preferencesInteractor
        )

        summaryPresenter.notifyOnViewReady()
        summaryPresenter.onSaveClick()

        verify(routesManager, times(1)).saveRoute(any())
        verify(summaryView, times(1)).finishActivity()
        verify(summaryView, times(1)).getRouteNameFromEditText()
    }

    @Test
    fun onSaveClick_whenNameNotAvailable_shouldApplyDefaultRouteName() {

        val extendedRouteData = ExtendedRouteData.Instance(
              null,
                0.0,
                0.0,
                0.0,
                0L,
                "",
                0L,
                0.0,
                0.0,
                0.0,
                listOf(),
                listOf()
        )

        val routesManager: RoutesManager = mock {
            on { getTempRoute(any()) }.then { extendedRouteData }
            on { saveRoute(any())}.then {
                Assert.assertEquals(resourceResolver.resolve(Text.DEFAULT_ROUTE_NAME), it.getArgument<ExtendedRouteData>(0).name)
            }
        }

        val mockSummaryView: SummaryView = mock { on { getRouteNameFromEditText() }.then { "" } }

        val summaryPresenter =  AfterTrackingSummaryPresenter(
                mockSummaryView,
                routesManager,
                resourceResolver,
                preferencesInteractor
        )

        summaryPresenter.notifyOnViewReady()
        summaryPresenter.onSaveClick()

        verify(resourceResolver, atLeastOnce()).resolve(any<Text>())
        verify(mockSummaryView, times(1)).getRouteNameFromEditText()
    }

    @Test
    fun onSaveClick_whenNameAvailable_shouldNotApplyDefaultRouteName() {

        val extendedRouteData = ExtendedRouteData.Instance(
                null,
                0.0,
                0.0,
                0.0,
                0L,
                "",
                0L,
                0.0,
                0.0,
                0.0,
                listOf(),
                listOf()
        )

        val expectedName = "Just name"
        val routesManager: RoutesManager = mock {
            on { getTempRoute(any()) }.then { extendedRouteData }
            on { saveRoute(any()) }.then { Assert.assertEquals(expectedName, it.getArgument<ExtendedRouteData>(0).name) }
        }

        val mockSummaryView: SummaryView = mock { on { getRouteNameFromEditText() }.then { expectedName } }

        val summaryPresenter =  AfterTrackingSummaryPresenter(
                mockSummaryView,
                routesManager,
                resourceResolver,
                preferencesInteractor
        )

        summaryPresenter.notifyOnViewReady()
        summaryPresenter.onSaveClick()

        verify(resourceResolver, never()).resolve(any<Text>())
        verify(mockSummaryView, times(1)).getRouteNameFromEditText()
    }

    @Test
    fun onExitRequest_shouldShowRejectDialogAndFinishActivityOnCallback() {

        val summaryView: SummaryView = mock {
            on { showRejectDialog(any()) }.then { (it.getArgument(0) as () -> Unit).invoke() }
        }

        val summaryPresenter = AfterTrackingSummaryPresenter(
                summaryView,
                mock { on { getTempRoute(any()) }.then { routeData }},
                mock { },
                mock {
                    on { trackingSpeedUnit }.then { AppUnit.Speed.KM_H }
                    on { trackingDistanceUnit }.then { AppUnit.Distance.KM }
                   }
        )

        summaryPresenter.notifyOnViewReady()
        summaryPresenter.onExitRequest()

        verify(summaryView, times(1)).showRejectDialog(any())
        verify(summaryView, times(1)).finishActivity()
    }

    @Test
    fun onRejectClick_shouldShowRejectDialogAndFinishActivityOnCallback() {

        val summaryView: SummaryView = mock {
            on { showRejectDialog(any()) }.then { (it.getArgument(0) as () -> Unit).invoke() }
        }

        val summaryPresenter = AfterTrackingSummaryPresenter(
                summaryView,
                mock { on { getTempRoute(any()) }.then { routeData }},
                mock { },
                mock {
                    on { trackingSpeedUnit }.then { AppUnit.Speed.KM_H }
                    on { trackingDistanceUnit }.then { AppUnit.Distance.KM }
                }
        )

        summaryPresenter.notifyOnViewReady()
        summaryPresenter.onRejectClick()

        verify(summaryView, times(1)).showRejectDialog(any())
        verify(summaryView, times(1)).finishActivity()
    }

}