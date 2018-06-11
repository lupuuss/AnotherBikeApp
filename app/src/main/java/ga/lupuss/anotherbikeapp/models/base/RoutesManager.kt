package ga.lupuss.anotherbikeapp.models.base

import ga.lupuss.anotherbikeapp.models.firebase.OnDocumentChanged
import ga.lupuss.anotherbikeapp.models.dataclass.ExtendedRouteData
import ga.lupuss.anotherbikeapp.models.dataclass.ShortRouteData

interface RoutesManager {

    interface OnRequestMoreShortRouteDataListener {
        fun onDataEnd()
        fun onFail(exception: Exception)
    }

    interface OnRequestExtendedRouteDataListener {
        fun onDataOk(routeData: ExtendedRouteData)
        fun onMissingData()
    }

    val routeReferenceSerializer: RouteReferenceSerializer

    fun addRoutesDataChangedListener(onRoutesChangedListener: OnDocumentChanged)
    fun removeOnRoutesDataChangedListener(onRoutesChangedListener: OnDocumentChanged)
    fun requestMoreShortRouteData(
            onRequestMoreShortRouteDataListener: OnRequestMoreShortRouteDataListener?,
            requestOwner: Any? = null
    )
    fun readShortRouteData(position: Int): ShortRouteData

    fun shortRouteDataCount(): Int
    fun requestExtendedRoutesData(routeReference: RouteReference,
                                  onRequestExtendedRouteDataListener: OnRequestExtendedRouteDataListener?,
                                  requestOwner: Any? = null)
    fun saveRoute(routeData: ExtendedRouteData)
    fun keepTempRoute(routeData: ExtendedRouteData)
    fun getTempRoute(): ExtendedRouteData?
    fun clearTempRoute()
    fun getRouteReference(position: Int): RouteReference
    fun deleteRoute(routeReference: RouteReference)
    fun changeName(routeReference: RouteReference, routeNameFromEditText: String)
}