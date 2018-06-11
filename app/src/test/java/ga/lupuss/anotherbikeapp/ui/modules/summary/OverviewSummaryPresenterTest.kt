package ga.lupuss.anotherbikeapp.ui.modules.summary

import com.nhaarman.mockito_kotlin.*
import ga.lupuss.anotherbikeapp.Text
import ga.lupuss.anotherbikeapp.models.base.PreferencesInteractor
import ga.lupuss.anotherbikeapp.models.base.RouteReference
import ga.lupuss.anotherbikeapp.models.base.RoutesManager
import ga.lupuss.anotherbikeapp.models.base.StringsResolver
import org.junit.Test
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

class OverviewSummaryPresenterTest {

    private val expectedName = "Just name"
    private val summaryView: SummaryView = mock {
        on { getRouteNameFromEditText() }.then { expectedName }
        on { showDeleteDialog(any()) }.then { (it.getArgument(0) as () -> Unit).invoke() }
        on { showUnsavedStateDialog(any()) }.then { (it.getArgument(0) as () -> Unit).invoke() }
    }
    private val routesManager: RoutesManager = mock {  }
    private val resolvedString = "resolvedString"
    private val stringsResolver: StringsResolver = mock { on { resolve(Text.DEFAULT_ROUTE_NAME) }.then { resolvedString } }
    private val preferencesInteractor: PreferencesInteractor = mock {  }
    private val routeReference: RouteReference = mock { }

    private val summaryPresenter =
            OverviewSummaryPresenter(summaryView, routesManager, stringsResolver, preferencesInteractor)

    private fun mockPrivateRouteReference(summaryPresenter: OverviewSummaryPresenter) {
        (summaryPresenter::class.memberProperties
                .find { it.name == "routeReference" }
                ?.apply { isAccessible = true }
                as KMutableProperty<*>)
                .setter
                .call(summaryPresenter, routeReference)
    }

    private fun setPrivateNameValue(summaryPresenter: OverviewSummaryPresenter, name: String) {
        (summaryPresenter::class.memberProperties
                .find { it.name == "name" }
                ?.apply { isAccessible = true }
                as? KMutableProperty<*>)
                ?.setter
                ?.call(summaryPresenter, name)
    }

    private fun getPrivateNameValue(summaryPresenter: OverviewSummaryPresenter): String {
        return summaryPresenter::class.memberProperties
                .find { it.name == "name" }
                ?.apply { isAccessible = true }
                ?.getter
                ?.call(summaryPresenter) as String
    }

    @Test
    fun passRouteReference_shouldSetRouteReference() {
        summaryPresenter.passRouteReference(routeReference)

        assert((summaryPresenter::class.memberProperties
                .find { it.name == "routeReference" }
                ?.apply { isAccessible = true }
                ?.getter
                ?.call(summaryPresenter) as RouteReference) == routeReference)
    }

    @Test
    fun notifyOnViewReady_shouldPrepareSummaryViewAndRequestExtendedRouteData() {

        mockPrivateRouteReference(summaryPresenter)

        summaryPresenter.notifyOnViewReady()

        verify(summaryView, times(1)).isRouteEditLineVisible = false
        verify(summaryView, times(1)).isProgressBarVisible = true
        verify(summaryView, times(1)).isStatsFragmentVisible = false
        verify(summaryView, times(1)).isSaveActionVisible = false
        verify(summaryView, times(1)).isRejectActionVisible = false

        verify(routesManager, times(1)).requestExtendedRoutesData(any(), any(), any())
    }

    @Test
    fun onDataOk_shouldPrepareViewAndShowData() {

        summaryPresenter.onDataOk(mock { on { name }.then{ "" }})

        verify(summaryView, times(1)).isRouteEditLineVisible = true
        verify(summaryView, times(1)).isProgressBarVisible = false
        verify(summaryView, times(1)).isStatsFragmentVisible = true
        verify(summaryView, times(1)).isRejectActionVisible = true
        verify(summaryView, never()).isSaveActionVisible = false

        verify(summaryView, times(1)).showRouteLine(any())
        verify(summaryView, times(1)).showStatistics(any())
        verify(summaryView, times(1)).setNameLabelValue(any())

    }

    @Test
    fun onDataOk_shouldSetNameProperly() {

        summaryPresenter.onDataOk( mock { on { name }.then { null } })
        assert(getPrivateNameValue(summaryPresenter) == resolvedString)
        verify(stringsResolver, times(1)).resolve(Text.DEFAULT_ROUTE_NAME)

        summaryPresenter.onDataOk( mock { on { name }.then { expectedName }})
        assert(getPrivateNameValue(summaryPresenter) == expectedName)
        verify(stringsResolver, times(1)).resolve(Text.DEFAULT_ROUTE_NAME) // as it was before second call
    }

    @Test
    fun onMissingData_shouldPrepareView() {

        summaryPresenter.onMissingData()
        verify(summaryView, times(1)).isProgressBarVisible = false
        verify(summaryView, times(1)).isStatsFragmentVisible = true
    }

    @Test
    fun onSaveClick_changeNameAndDisableSaveAction() {

        mockPrivateRouteReference(summaryPresenter)
        summaryPresenter.notifyOnViewReady()
        summaryPresenter.onSaveClick()

        assert(expectedName == getPrivateNameValue(summaryPresenter))
        verify(routesManager, times(1)).changeName(routeReference, expectedName)
        verify(summaryView, times(2)).isSaveActionVisible = false
    }

    @Test
    fun onExitRequest_shouldFinishActivityIfNameNotInitialized() {

        // name is not initialized by default
        summaryPresenter.onExitRequest()
        verify(summaryView, times(1)).finishActivity()
        verify(summaryView, never()).showUnsavedStateDialog(any())
    }

    @Test
    fun onExitRequest_shouldFinishActivityIfNameEqualNameFromEditText() {

        setPrivateNameValue(summaryPresenter, expectedName)
        summaryPresenter.onExitRequest()
        verify(summaryView, times(1)).finishActivity()
        verify(summaryView, never()).showUnsavedStateDialog(any())
    }

    @Test
    fun onExitRequest_shouldShowUnsavedStateDialogIfNameInitializedAndNotEqualNameFormEditText() {

        setPrivateNameValue(summaryPresenter, "Any different value!")
        summaryPresenter.onExitRequest()

        verify(summaryView, times(1)).showUnsavedStateDialog(any())
        verify(summaryView, times(1)).finishActivity()
    }

    @Test
    fun onRejectClick_shouldShowDeleteDialogAndDeleteRouteOnCallback() {

        mockPrivateRouteReference(summaryPresenter)
        summaryPresenter.onRejectClick()

        verify(summaryView, times(1)).showDeleteDialog(any())
        verify(routesManager, times(1)).deleteRoute(routeReference)
        verify(summaryView, times(1)).finishActivity()
    }

    @Test
    fun onNameEditTextChanged_shouldEnableSaveActionIfStringsNotEqual(){

        setPrivateNameValue(summaryPresenter, "Any different value!")
        summaryPresenter.onNameEditTextChanged(expectedName)

        verify(summaryView, times(1)).isSaveActionVisible = true
    }

    @Test
    fun onNameEditTextChanged_shouldDisableSaveActionIfStringsEqual(){

        setPrivateNameValue(summaryPresenter, expectedName)
        summaryPresenter.onNameEditTextChanged(expectedName)

        verify(summaryView, times(1)).isSaveActionVisible = false
    }

    @Test
    fun onNameEditTextChanged_shouldNotFailIfNameIsNotInitialized() {

        // name is not initialized by default
        summaryPresenter.onNameEditTextChanged("")
        verify(summaryView, never()).isSaveActionVisible = any()
    }
}