package ga.lupuss.anotherbikeapp.ui.modules.summary

import com.nhaarman.mockito_kotlin.*
import ga.lupuss.anotherbikeapp.models.base.RouteReference
import ga.lupuss.anotherbikeapp.models.base.RouteReferenceSerializer
import ga.lupuss.anotherbikeapp.models.base.RoutesManager
import ga.lupuss.anotherbikeapp.models.firebase.FirebaseRouteReference
import org.junit.Test
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

class MainSummaryPresenterTest {

    @Test
    fun initMode_shouldSelectProperPresenterImplementation() {

        var summaryPresenter = MainSummaryPresenter(
                mock {  },
                mock {  },
                mock {  },
                mock {  }
        )

        summaryPresenter.initMode(SummaryPresenter.Mode.AFTER_TRACKING_SUMMARY, null)

        var obj: SummaryPresenter? =
                summaryPresenter::class
                        .memberProperties
                        .find { it.name == "subPresenter" }
                        ?.apply { isAccessible = true }
                        ?.getter
                        ?.call(summaryPresenter) as SummaryPresenter


        assert(obj is AfterTrackingSummaryPresenter)


        summaryPresenter = MainSummaryPresenter(
                mock {  },
                mock { on { routeReferenceSerializer }.then {
                    mock<RouteReferenceSerializer> { on { deserialize(any()) }.then { FirebaseRouteReference(mock{},mock{}, mock{}) } }
                } },
                mock {  },
                mock {  })

        val string = "referencejson"
        summaryPresenter.initMode(SummaryPresenter.Mode.OVERVIEW, string)

        obj = summaryPresenter::class
                .memberProperties
                .find { it.name == "subPresenter" }
                ?.apply { isAccessible = true }
                ?.getter
                ?.call(summaryPresenter) as SummaryPresenter


        assert(obj is OverviewSummaryPresenter)

    }

    @Test
    fun initMode_shouldPassRouteReferenceToOverviewSummaryPresenter() {

        val routeReference: RouteReference = mock {  }

        val summaryPresenter = MainSummaryPresenter(
                mock {  },
                mock { on { routeReferenceSerializer }
                        .then { mock<RouteReferenceSerializer>{
                            on { deserialize(any()) }.then { routeReference } } } },
                mock {  },
                mock {  })

        summaryPresenter.initMode(SummaryPresenter.Mode.OVERVIEW, "")

        val obj: OverviewSummaryPresenter =
                summaryPresenter::class.memberProperties
                        .find { it.name == "subPresenter" }
                        ?.apply { isAccessible = true }
                        ?.getter
                        ?.call(summaryPresenter)
                        as OverviewSummaryPresenter

        assert((obj::class.memberProperties
                .find { it.name == "routeReference" }
                ?.apply { isAccessible = true }
                ?.getter
                ?.call(obj) as? RouteReference) == routeReference)
    }

    @Test
    fun notifyOnDestroy_shouldClearTempRouteIfFinishing() {

        val routesManager: RoutesManager = mock {  }
        val summaryPresenter = MainSummaryPresenter(mock { }, routesManager, mock {  }, mock {  })

        (summaryPresenter::class.memberProperties
                .find { it.name == "subPresenter" }
                ?.apply { this.isAccessible = true }
                as KMutableProperty<*>)
                .setter
                .call(summaryPresenter, mock<SummaryPresenter>{ })

        summaryPresenter.notifyOnDestroy(false)
        verify(routesManager, never()).clearTempRoute()

        summaryPresenter.notifyOnDestroy(true)
        verify(routesManager, times(1)).clearTempRoute()

    }

    @Test
    fun otherMethods_shouldBeDelegatedToSubPresenter() {

        val summaryPresenter = MainSummaryPresenter(mock {  }, mock {  }, mock {  }, mock {  })

        val subPresenter: SummaryPresenter = mock {  }

        (summaryPresenter::class.memberProperties
                .find { it.name == "subPresenter" }
                ?.apply { isAccessible = true }
                as KMutableProperty<*>)
                .setter
                .call(summaryPresenter, subPresenter)

        summaryPresenter.notifyOnViewReady()
        verify(subPresenter, times(1)).notifyOnViewReady()

        reset(subPresenter)

        summaryPresenter.onSaveClick()
        verify(subPresenter, times(1)).onSaveClick()

        reset(subPresenter)

        summaryPresenter.onExitRequest()
        verify(subPresenter, times(1)).onExitRequest()

        reset(subPresenter)

        summaryPresenter.onRejectClick()
        verify(subPresenter, times(1)).onRejectClick()

        reset(subPresenter)

        summaryPresenter.onNameEditTextChanged(null)
        verify(subPresenter, times(1)).onNameEditTextChanged(null)
    }
}