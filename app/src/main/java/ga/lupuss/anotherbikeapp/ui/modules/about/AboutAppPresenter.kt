package ga.lupuss.anotherbikeapp.ui.modules.about

import ga.lupuss.anotherbikeapp.FIREBASE_URL
import ga.lupuss.anotherbikeapp.FLAT_ICON_URL
import ga.lupuss.anotherbikeapp.OPEN_WEATHER_MAP_URL
import ga.lupuss.anotherbikeapp.base.Presenter
import javax.inject.Inject

class AboutAppPresenter @Inject constructor(aboutAppView: AboutAppView) : Presenter<AboutAppView>() {
    init {
        view = aboutAppView
    }


    fun onClickFirebase() {
        view.redirectToUrl(FIREBASE_URL)
    }

    fun onClickOpenWeatherMap() {
        view.redirectToUrl(OPEN_WEATHER_MAP_URL)
    }

    fun onClickFlatIcon() {

        view.redirectToUrl(FLAT_ICON_URL)
    }
}