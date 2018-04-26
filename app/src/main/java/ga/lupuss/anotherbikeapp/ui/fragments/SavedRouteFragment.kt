package ga.lupuss.anotherbikeapp.ui.fragments


import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.PolylineOptions
import ga.lupuss.anotherbikeapp.AnotherBikeApp
import ga.lupuss.anotherbikeapp.R
import ga.lupuss.anotherbikeapp.models.pojo.SerializableRouteData
import ga.lupuss.anotherbikeapp.models.trackingservice.statisticsmanager.statistics.Statistic
import kotlinx.android.synthetic.main.fragment_saved_route.*

/**
 * A simple [Fragment] subclass.
 * Use the [SavedRouteFragment.newInstance] factory method to
 * create an instance of this fragment.
 *
 */
class SavedRouteFragment : Fragment(), OnMapReadyCallback {

    private lateinit var routeData: SerializableRouteData
    private lateinit var statisticsMap: Map<Statistic.Name, Statistic>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val path = arguments.getString(ROUTE_DATA_PATH)

        routeData = AnotherBikeApp.get(this.activity.application)
                .component.providesRoutesKeeper().readRoute(path)
        statisticsMap = routeData.getStatisticsMap(Statistic.Unit.KM_H, Statistic.Unit.KM)

    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(
                R.layout.fragment_saved_route, container, false
        ) as ViewGroup

        durationStat.text = statisticsMap[Statistic.Name.DURATION]?.getValue(this.context)
        distanceStat.text = statisticsMap[Statistic.Name.DISTANCE]?.getValue(this.context)
        speedStat.text = statisticsMap[Statistic.Name.AVG_SPEED]?.getValue(this.context)

        return view
    }

    override fun onMapReady(map: GoogleMap?) {

        map?.let {
            it.addPolyline(PolylineOptions().apply { addAll(routeData.savedRoute) })
        }
    }

    companion object {

        private const val ROUTE_DATA_PATH = "ROUTE_DATA_PATH"

        @JvmStatic
        fun newInstance(path: String) = SavedRouteFragment().apply {

            arguments.apply {

                putString(ROUTE_DATA_PATH, path)
            }
        }
    }
}
