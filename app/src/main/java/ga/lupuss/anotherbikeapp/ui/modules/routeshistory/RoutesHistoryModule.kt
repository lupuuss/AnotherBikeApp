package ga.lupuss.anotherbikeapp.ui.modules.routeshistory

import dagger.Module
import dagger.Provides

@Module
class RoutesHistoryModule(routesHistoryView: RoutesHistoryView) {

    val routesHistoryView: RoutesHistoryView = routesHistoryView
        @Provides
        @RoutesHistoryComponentScope
        get
}