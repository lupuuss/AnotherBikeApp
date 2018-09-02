package ga.lupuss.anotherbikeapp.models.firebase.pojo

import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.GeoPoint

class FirebasePoints {

    var points: List<GeoPoint>? = null
    @Suppress("unused")
    var routeId: String? = null
    var userId: String? = null
    fun pointsAsLatLngList(): MutableList<LatLng>? {

        if (points == null) return null

        val list = mutableListOf<LatLng>()

        points!!.forEach {
            list.add(LatLng(it.latitude, it.longitude))
        }

        return list
    }
}