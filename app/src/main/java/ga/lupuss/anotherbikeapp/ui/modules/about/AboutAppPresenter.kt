package ga.lupuss.anotherbikeapp.ui.modules.about

import ga.lupuss.anotherbikeapp.base.Presenter
import javax.inject.Inject

class AboutAppPresenter @Inject constructor(aboutAppView: AboutAppView) : Presenter<AboutAppView>() {

    init {
        view = aboutAppView
    }

}