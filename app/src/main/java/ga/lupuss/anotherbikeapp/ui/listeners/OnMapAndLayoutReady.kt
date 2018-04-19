package ga.lupuss.anotherbikeapp.ui.listeners

class OnMapAndLayoutReady(private val onMapAndLayoutReady: () -> Unit) {

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
            onMapAndLayoutReady.invoke()
        }
    }

}