package ga.lupuss.anotherbikeapp.ui.modules.tracking

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.util.TypedValue
import android.view.*
import android.widget.*
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.tinsuke.icekick.extension.freezeInstanceState
import com.tinsuke.icekick.extension.serialState
import com.tinsuke.icekick.extension.unfreezeInstanceState

import ga.lupuss.anotherbikeapp.R
import ga.lupuss.anotherbikeapp.base.BaseActivity
import ga.lupuss.anotherbikeapp.models.trackingservice.statisticsmanager.statistics.Statistic
import ga.lupuss.anotherbikeapp.models.trackingservice.TrackingService
import ga.lupuss.anotherbikeapp.ui.extensions.ViewExtensions
import ga.lupuss.anotherbikeapp.ui.extensions.getColorForAttr
import ga.lupuss.anotherbikeapp.ui.extensions.setText
import ga.lupuss.anotherbikeapp.ui.fragments.StatsFragment
import kotlinx.android.synthetic.main.activity_tracking.*
import kotlinx.android.synthetic.main.activity_tracking_short_stats_container.*
import timber.log.Timber
import javax.inject.Inject


class TrackingActivity : BaseActivity(),
        OnMapReadyCallback, TrackingView {

    @Inject
    lateinit var trackingPresenter: TrackingPresenter

    private val shortStatsListToDisplay = listOf(
            Statistic.Name.DURATION,
            Statistic.Name.AVG_SPEED,
            Statistic.Name.DISTANCE
    )

    private val defaultMapZoom = 16.5F
    private lateinit var map: GoogleMap

    private var trackLine: Polyline? = null
    private lateinit var trackLineOptions: PolylineOptions

    private lateinit var defaultMarkerOptions: MarkerOptions

    private var isInfoContainerExpand by serialState(true)

    private var infoContainerVisibility: Int = View.INVISIBLE
        set(value) {

            statsContainer.visibility = value
            statsContainerExpandButton.visibility = value
            statsContainerExpandButtonIcon.visibility = value
            field = value
        }


    private val onMapAnLayoutReady = OnMapAndLayoutReady {

        infoContainerVisibility = View.VISIBLE
        adjustCameraToInfoContainer()
        setOnTouchInfoContainerExpandButton()
    }

    @SuppressLint("ShowToast")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tracking)
        activateToolbar(toolbarMain)
        setResult(Result.NOT_DONE)

        DaggerTrackingComponent.builder().trackingModule(
                TrackingModule(this, getIBinderFromIntent()
                )
        ).build().inject(this)

        unfreezeInstanceState(savedInstanceState)

        //init google map
        (supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment).getMapAsync(this)

        shortStatsContainer.alpha = if (isInfoContainerExpand) 0F else 1F

        infoContainerVisibility = View.INVISIBLE

        statsContainer.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {

            override fun onGlobalLayout() {

                setInfoContainerExpandState(isInfoContainerExpand, false)
                onMapAnLayoutReady.layoutReady()
                statsContainer.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
    }

    private fun getIBinderFromIntent(): TrackingService.ServiceBinder {

        val bundle = intent.getBundleExtra(ARG_MAIN_BUNDLE)
        bundle ?: throw IllegalStateException("No bundle")
        return bundle.getBinder(ARG_BINDER) as TrackingService.ServiceBinder
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.activity_tracking_menu, menu)
        return true
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {

        trackLineOptions =
                PolylineOptions().color(theme.getColorForAttr(R.attr.trackLineColor)).width(15F)

        defaultMarkerOptions = MarkerOptions().icon(
                ViewExtensions.getDefaultMarkerIconForColor(theme.getColorForAttr(R.attr.markersColor))
        )

        map = googleMap

        onMapAnLayoutReady.mapReady()

        map.apply {
            isMyLocationEnabled = true
            setMapStyle(MapStyleOptions(getGoogleMapStyleFromTheme()))
            setOnMapClickListener { trackingPresenter.onGoogleMapClick() }
            setOnMyLocationButtonClickListener({
                trackingPresenter.onMyLocationButtonClick()
                true
            })
        }

        trackingPresenter.notifyOnViewReady()
    }

    private fun getGoogleMapStyleFromTheme(): String {

        val typedValue = TypedValue()
        theme.resolveAttribute(R.attr.googleMapStyleJson, typedValue, true)

        return typedValue.string as String
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        freezeInstanceState(outState!!)
    }

    override fun onDestroy() {
        super.onDestroy()

        Timber.d("Tracking activity destroyed!")
        trackingPresenter.notifyOnDestroy(isFinishing)
    }

    // menu onClicks

    @Suppress("UNUSED_PARAMETER")
    fun onClickFinishTracking(menuItem: MenuItem?) {

        trackingPresenter.onClickFinishTracking()
    }

    // onTouch

    private fun setOnTouchInfoContainerExpandButton() {
        statsContainerExpandButton.setOnTouchListener( // handle show/hide animations
                StatsContainerOnTouchListener(
                        this,
                        (statsContainer as FrameLayout),
                        statsContainerExpandButton,
                        (shortStatsContainer as LinearLayout),
                        map,
                        isInfoContainerExpand
                ) {
                    isInfoContainerExpand = it
                }
        )
    }

    // TrackingView Impl

    override fun prepareMapToTrack(points: List<LatLng>) {

        trackLine = map.addPolyline(trackLineOptions.addAll(points))
        map.addMarker(defaultMarkerOptions.position(points[0]).title(getString(R.string.start)))
        map.moveCamera(CameraUpdateFactory.zoomTo(defaultMapZoom))
        map.moveCamera(CameraUpdateFactory.newLatLng(points.last()))
    }

    override fun updateTrackLine(points: List<LatLng>) {

        trackLine?.points = points
    }

    override fun updateStats(statistics: Map<Statistic.Name, Statistic>) {
        updateShortStats(statistics)
        updateInfoFragmentStats(statistics)
    }

    private fun updateShortStats(stats: Map<Statistic.Name, Statistic>) {

        if (emptyShortStatsText != null) {

            initShortStats(stats)

        } else {

            updateExistingShortStats(stats)
        }
    }

    private fun initShortStats(stats: Map<Statistic.Name, Statistic>) {

        (shortStatsContainer as ViewGroup).removeAllViews()

        for (statName in shortStatsListToDisplay) {

            (shortStatsContainer as ViewGroup).addView(ViewExtensions.createTextViewStatWithTag(
                    layoutInflater,
                    (shortStatsContainer as ViewGroup),
                    R.layout.activity_tracking_short_stat,
                    statName,
                    stats[statName]!!
            ))
        }

    }

    private fun updateExistingShortStats(stats: Map<Statistic.Name, Statistic>) {

        for (statName in shortStatsListToDisplay) {

            shortStatsContainer.findViewWithTag<TextView>(statName).setText(statName, stats[statName]!!)
        }
    }

    private fun updateInfoFragmentStats(stats: Map<Statistic.Name, Statistic>) {

        (supportFragmentManager.findFragmentById(R.id.statsFragment) as StatsFragment?)
                ?.updateStats(stats)
    }

    override fun setInfoWaitForLocationVisibility(visibility: Int) {

        infoWaitForLocation?.visibility = visibility
    }

    override fun isTrackLineReady() = trackLine != null

    override fun moveMapCamera(latLng: LatLng) {

        map.animateCamera(CameraUpdateFactory.newLatLng(latLng))
    }

    private fun adjustCameraToInfoContainer() {

        val isPortrait = resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT

        if (isInfoContainerExpand) {

            if (isPortrait) {

                map.setPadding(0, 0, 0, statsContainer.height)

            } else {

                map.setPadding(0, 0, statsContainer.width, 0)
            }

        } else {

            map.setPadding(0, 0, 0, 0)
        }
    }

    private fun setInfoContainerExpandState(expand: Boolean, adjustCamera: Boolean) {

        val isPortrait = resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT

        statsContainerExpandButton
                .findViewById<ImageView>(R.id.statsContainerExpandButtonIcon)
                .rotation = if (expand) 0F else 180F // rotate button icon if necessary

        if (isPortrait) {

            statsContainer.translationY = if (expand) 0F else statsContainer.height.toFloat()
            statsContainerExpandButton.translationY = if (expand) 0F else statsContainer.height.toFloat()

        } else {

            statsContainer.translationX = if (expand) 0F else statsContainer.width.toFloat()
            statsContainerExpandButton.translationX = if (expand) 0F else statsContainer.width.toFloat()
        }

        if (adjustCamera) {

            adjustCameraToInfoContainer()
        }

    }

    override fun showFinishTrackingDialog(onYesAction: () -> Unit) {

        AlertDialog.Builder(this)
                .setTitle(R.string.warning)
                .setMessage(R.string.message_finish_tracking)
                .setPositiveButton(R.string.yes, { _, _ ->
                    onYesAction.invoke()
                })
                .setNegativeButton(R.string.cancel, null)
                .show()
    }

    override fun finishActivityWithResult(result: Int) {
        setResult(result)
        finish()
    }

    // --

    companion object {

        // the activity parameters
        private const val ARG_MAIN_BUNDLE = "main bundle"
        private const val ARG_BINDER = "IBinder"

        fun newIntent(context: Context, serviceBinder: TrackingService.ServiceBinder) =
                Intent(context, TrackingActivity::class.java).apply {

                    val bundle = Bundle()
                    bundle.putBinder(ARG_BINDER, serviceBinder)
                    putExtra(ARG_MAIN_BUNDLE, bundle)
                }
    }

    /** Possible results codes */
    class Result {

        companion object {
            const val NOT_DONE = 0
            const val NO_DATA_DONE = 1
            const val DONE = 2
        }
    }
}
