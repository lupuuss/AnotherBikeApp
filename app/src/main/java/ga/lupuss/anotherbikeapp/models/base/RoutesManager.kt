package ga.lupuss.anotherbikeapp.models.base

import ga.lupuss.anotherbikeapp.models.firebase.OnDataSetChanged
import ga.lupuss.anotherbikeapp.models.dataclass.ExtendedRouteData
import ga.lupuss.anotherbikeapp.models.dataclass.ShortRouteData

interface RoutesManager {

    enum class Slot {
        MAIN_TO_SUMMARY, SUMMARY_BACKUP
    }

    interface OnRequestMoreShortRouteDataListener {
        fun onRequestSuccess()
        fun onDataEnd()
        fun onFail(exception: Exception)
    }

    interface OnRequestExtendedRouteDataListener {
        fun onDataOk(routeData: ExtendedRouteData)
        fun onMissingData()
    }

    val routeReferenceSerializer: RouteReferenceSerializer

    fun refresh(onRequestMoreShortRouteDataListener: RoutesManager.OnRequestMoreShortRouteDataListener?, requestOwner: Any?)
    fun addRoutesDataChangedListener(onRoutesChangedListener: OnDataSetChanged)
    fun removeOnRoutesDataChangedListener(onRoutesChangedListener: OnDataSetChanged)
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
    fun keepTempRoute(slot: Slot, routeData: ExtendedRouteData)
    fun getTempRoute(slot: Slot): ExtendedRouteData?
    fun clearTempRoute(slot: Slot)
    fun getRouteReference(position: Int): RouteReference
    fun deleteRoute(routeReference: RouteReference)
    fun changeName(routeReference: RouteReference, routeNameFromEditText: String)
}