package ga.lupuss.anotherbikeapp.ui.modules.summary

import dagger.Module
import dagger.Provides
import ga.lupuss.anotherbikeapp.models.base.PreferencesInteractor
import ga.lupuss.anotherbikeapp.models.base.RoutesManager
import ga.lupuss.anotherbikeapp.models.base.ResourceResolver

@Module
class SummaryModule(summaryView: SummaryView) {

    val summaryView = summaryView
        @Provides
        @SummaryComponentScope
        get

    @Provides
    fun providesSummaryPresenter(summaryView: SummaryView,
                                 routesManager: RoutesManager,
                                 resourceResolver: ResourceResolver,
                                 preferencesInteractor: PreferencesInteractor): MainSummaryPresenter =
            MainSummaryPresenter(summaryView, routesManager, resourceResolver, preferencesInteractor)
}