package ga.lupuss.anotherbikeapp.ui.modules.tracking

class OnMapAndLayoutReady(private val onMapAndLayoutReady: Listener) {

    interface Listener {

        fun onMapAndLayoutReady()
    }

    private var isMapReady = false
    private var isLayoutReady = false

    fun mapReady() {
        isMapReady = true
        checkChanges()
    }

    fun layoutReady() {
        isLayoutReady = true
        checkChanges()
    }

    private fun checkChanges() {
        if (isMapReady && isLayoutReady) {
            onMapAndLayoutReady.onMapAndLayoutReady()
        }
    }

}