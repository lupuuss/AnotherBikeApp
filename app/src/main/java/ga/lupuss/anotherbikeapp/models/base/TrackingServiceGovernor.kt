package ga.lupuss.anotherbikeapp.models.base

import ga.lupuss.anotherbikeapp.models.trackingservice.TrackingService

abstract class TrackingServiceGovernor {

    var isServiceActive = false
        protected set (value) {
            field = value

            serviceActivityListeners.forEach { (_, listener) ->

                listener.onServiceActivityChanged(value)
            }
        }

    interface OnServiceActivityChangesListener {
        fun onServiceActivityChanged(state: Boolean)
    }

    private val serviceActivityListeners = mutableMapOf<Any, OnServiceActivityChangesListener>()

    abstract val serviceBinder: TrackingService.ServiceBinder?
    abstract val serviceInteractor: TrackingServiceInteractor?
    abstract fun startTracking(onSuccess: (() -> Unit)?, onNoPermission: (() -> Unit)?)
    abstract fun stopTracking()
    abstract fun destroy(isFinishing: Boolean)

    fun addOnServiceActivityChangesListener(owner: Any,
                                            onServiceActivityChangesListener: OnServiceActivityChangesListener) {
        serviceActivityListeners[owner] = onServiceActivityChangesListener
    }

    fun removeOnServiceActivityChangesListener(owner: Any) {

        serviceActivityListeners.remove(owner)
    }
}