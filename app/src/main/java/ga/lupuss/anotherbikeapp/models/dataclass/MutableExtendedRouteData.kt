package ga.lupuss.anotherbikeapp.models.dataclass

import com.google.android.gms.maps.model.LatLng

interface MutableExtendedRouteData : ExtendedRouteData, MutableRouteData {

    override val points: MutableList<LatLng>

    class Instance (
            override var name: String? = null,
            override var distance: Double = 0.0,
            override var avgSpeed: Double = 0.0,
            override var maxSpeed: Double = 0.0,
            override var duration: Long = 0,
            override var startTimeStr: String = "",
            override var startTime: Long = 0L,
            override var avgAltitude: Double = 0.0,
            override var maxAltitude: Double = 0.0,
            override var minAltitude: Double = 0.0,
            override var pictures: List<String>?,
            override val points: MutableList<LatLng> = mutableListOf()
    ) : MutableExtendedRouteData
}
