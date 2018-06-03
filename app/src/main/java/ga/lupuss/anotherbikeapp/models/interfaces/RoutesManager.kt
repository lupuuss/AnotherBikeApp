package ga.lupuss.anotherbikeapp.models.interfaces

import ga.lupuss.anotherbikeapp.models.firebase.OnDocumentChanged
import ga.lupuss.anotherbikeapp.models.dataclass.ExtendedRouteData
import ga.lupuss.anotherbikeapp.models.dataclass.ShortRouteData

interface RoutesManager {

    val routeReferenceSerializer: RouteReferenceSerializer

    fun addRoutesDataChangedListener(onRoutesChangedListener: OnDocumentChanged)
    fun removeOnRoutesDataChangedListener(onRoutesChangedListener: OnDocumentChanged)
    fun requestMoreShortRouteData(onDataEnd: (() -> Unit)?, onFail: ((Exception) -> Unit)?)
    fun readShortRouteData(position: Int): ShortRouteData

    fun shortRouteDataCount(): Int
    fun requestExtendedRoutesData(routeReference: RouteReference,
                                  onDataOk: ((ExtendedRouteData) -> Unit)?,
                                  onDataFail: ((Exception) -> Unit)?)
    fun saveRoute(routeData: ExtendedRouteData)
    fun keepTempRoute(routeData: ExtendedRouteData)
    fun getTempRoute(): ExtendedRouteData?
    fun clearTempRoute()
    fun getRouteReference(position: Int): RouteReference
    fun deleteRoute(routeReference: RouteReference)
}