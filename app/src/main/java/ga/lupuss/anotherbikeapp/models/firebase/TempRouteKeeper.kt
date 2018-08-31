package ga.lupuss.anotherbikeapp.models.firebase

import ga.lupuss.anotherbikeapp.models.base.RoutesManager
import ga.lupuss.anotherbikeapp.models.dataclass.ExtendedRouteData
import javax.inject.Inject

class TempRouteKeeper @Inject constructor() {

    private val tempRoutes = mutableMapOf<RoutesManager.Slot, ExtendedRouteData>()

    fun keep(slot: RoutesManager.Slot, route: ExtendedRouteData) {

        tempRoutes[slot] = route
    }

    fun getRoute(slot: RoutesManager.Slot) = tempRoutes[slot]

    fun clear(slot: RoutesManager.Slot) {

        tempRoutes.remove(slot)
    }
}