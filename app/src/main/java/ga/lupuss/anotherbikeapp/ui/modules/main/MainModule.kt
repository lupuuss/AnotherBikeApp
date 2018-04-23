package ga.lupuss.anotherbikeapp.ui.modules.main

import dagger.Module
import dagger.Provides

@Module
class MainModule(view: MainView) {
    val mainView = view
        @Provides
        @MainComponentScope
        get
}