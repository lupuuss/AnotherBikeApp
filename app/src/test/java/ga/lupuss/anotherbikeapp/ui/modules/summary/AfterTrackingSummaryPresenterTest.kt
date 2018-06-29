package ga.lupuss.anotherbikeapp.ui.modules.summary

import com.google.android.gms.maps.model.LatLng
import com.nhaarman.mockito_kotlin.*
import ga.lupuss.anotherbikeapp.Text
import ga.lupuss.anotherbikeapp.models.base.PreferencesInteractor
import ga.lupuss.anotherbikeapp.models.base.RoutesManager
import ga.lupuss.anotherbikeapp.models.base.StringsResolver
import ga.lupuss.anotherbikeapp.models.dataclass.ExtendedRouteData
import ga.lupuss.anotherbikeapp.models.dataclass.Statistic
import org.junit.Test

class AfterTrackingSummaryPresenterTest {

    private val summaryView: SummaryView = mock { }

    private val routeData: ExtendedRouteData = mock {
        on { name }.then { "name" }
        on { points }.then { emptyList<LatLng>() }
        on { getStatisticsMap(Statistic.Unit.KM_H, Statistic.Unit.KM) }.then { mapOf<Statistic.Name, Statistic<*>>() }
    }

    private val stringsResolver: StringsResolver = mock {
        on { resolve(Text.DEFAULT_ROUTE_NAME) }.then { "Route" }
    }

    private val preferencesInteractor: PreferencesInteractor = mock {
        on { speedUnit }.then { Statistic.Unit.KM_H }
        on { distanceUnit }.then { Statistic.Unit.KM }
    }

    @Test
    fun notifyOnVieReady_whenAvailable_shouldShowRouteData() {

        val summaryPresenter = AfterTrackingSummaryPresenter(
                summaryView,
                mock { on { getTempRoute() }.then { routeData } },
                mock {  },
                mock {  }
        )

        summaryPresenter.notifyOnViewReady()

        verify(summaryView, times(1)).showRouteLine(routeData.points)
        verify(summaryView, times(1)).showStatistics(
                routeData.getStatisticsMap(Statistic.Unit.KM_H, Statistic.Unit.KM)
        )
        verify(summaryView, times(1)).setNameLabelValue(routeData.name ?: "")

    }

    @Test
    fun onSaveClick_shouldSaveRouteAndFinishActivity() {

        val routesManager: RoutesManager = mock { on { getTempRoute() }.then { routeData } }

        val summaryPresenter =  AfterTrackingSummaryPresenter(
                summaryView,
                routesManager,
                stringsResolver,
                mock { }
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
                listOf()
        )

        val routesManager: RoutesManager = mock { on { getTempRoute() }.then {
            extendedRouteData
        } }


        val mockSummaryView: SummaryView = mock { on { getRouteNameFromEditText() }.then { "" } }

        val summaryPresenter =  AfterTrackingSummaryPresenter(
                mockSummaryView,
                routesManager,
                stringsResolver,
                preferencesInteractor
        )

        summaryPresenter.notifyOnViewReady()
        summaryPresenter.onSaveClick()

        verify(stringsResolver, times(1)).resolve(any<Text>())
        assert(extendedRouteData.name == stringsResolver.resolve(Text.DEFAULT_ROUTE_NAME))
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
                listOf()
        )

        val routesManager: RoutesManager = mock { on { getTempRoute() }.then {
            extendedRouteData
        } }

        val expectedName = "Just name"
        val mockSummaryView: SummaryView = mock { on { getRouteNameFromEditText() }.then { expectedName } }

        val summaryPresenter =  AfterTrackingSummaryPresenter(
                mockSummaryView,
                routesManager,
                stringsResolver,
                preferencesInteractor
        )

        summaryPresenter.notifyOnViewReady()
        summaryPresenter.onSaveClick()

        verify(stringsResolver, never()).resolve(any<Text>())
        assert(extendedRouteData.name == expectedName)
        verify(mockSummaryView, times(1)).getRouteNameFromEditText()
    }

    @Test
    fun onExitRequest_shouldShowRejectDialogAndFinishActivityOnCallback() {

        val summaryView: SummaryView = mock {
            on { showRejectDialog(any()) }.then { (it.getArgument(0) as () -> Unit).invoke() }
        }

        val summaryPresenter = AfterTrackingSummaryPresenter(
                summaryView,
                mock {  },
                mock {  },
                mock {  }
        )

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
                mock {  },
                mock {  },
                mock {  }
        )

        summaryPresenter.onRejectClick()

        verify(summaryView, times(1)).showRejectDialog(any())
        verify(summaryView, times(1)).finishActivity()
    }

}