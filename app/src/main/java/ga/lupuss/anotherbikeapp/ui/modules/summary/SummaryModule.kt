package ga.lupuss.anotherbikeapp.ui.modules.summary

import dagger.Module
import dagger.Provides

@Module
class SummaryModule(summaryView: SummaryView) {

    val summaryView = summaryView
        @Provides
        @SummaryComponentScope
        get
}