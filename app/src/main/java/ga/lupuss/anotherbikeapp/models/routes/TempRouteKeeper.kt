package ga.lupuss.anotherbikeapp.models.routes

import ga.lupuss.anotherbikeapp.models.pojo.ExtendedRouteData
import javax.inject.Inject

class TempRouteKeeper @Inject constructor() {

    private var tempRoute: ExtendedRouteData? = null

    fun keep(route: ExtendedRouteData) {
        tempRoute = route
    }

    fun getRoute() = tempRoute

    fun clear() {

        tempRoute = null
    }
}