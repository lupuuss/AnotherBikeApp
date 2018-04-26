package ga.lupuss.anotherbikeapp.ui.extensions

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds

fun GoogleMap.fitToPoints(points: List<LatLng>, padding: Int, maxZoom: Float) {

    val latLngBoundsBuilder = LatLngBounds.builder()

    points.forEach { latLngBoundsBuilder.include(it) }

    moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBoundsBuilder.build(), padding))

    if (cameraPosition.zoom > maxZoom) {

        moveCamera(CameraUpdateFactory.zoomTo(maxZoom))
    }
}