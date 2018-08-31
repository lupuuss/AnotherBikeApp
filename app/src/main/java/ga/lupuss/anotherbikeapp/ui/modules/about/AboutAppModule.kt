package ga.lupuss.anotherbikeapp.ui.modules.about

import dagger.Module
import dagger.Provides

@Module
class AboutAppModule(aboutAppView: AboutAppView) {

    val aboutAppView = aboutAppView
        @Provides
        @AboutAppComponentScope
        get
}