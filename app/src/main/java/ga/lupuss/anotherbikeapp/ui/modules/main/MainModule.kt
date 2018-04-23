package ga.lupuss.anotherbikeapp.ui.modules.main

import dagger.Module
import dagger.Provides

@Module
class MainModule(view: MainPresenter.IView) {
    val view = view
        @Provides
        @MainComponentScope
        get
}