package ga.lupuss.anotherbikeapp.ui.modules.tracking

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.drawable.Drawable
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
import ga.lupuss.anotherbikeapp.AnotherBikeApp

import ga.lupuss.anotherbikeapp.R
import ga.lupuss.anotherbikeapp.base.BaseActivity
import ga.lupuss.anotherbikeapp.models.dataclass.Statistic
import ga.lupuss.anotherbikeapp.models.trackingservice.TrackingService
import ga.lupuss.anotherbikeapp.ui.extensions.ViewExtensions
import ga.lupuss.anotherbikeapp.ui.extensions.getColorForAttr
import ga.lupuss.anotherbikeapp.ui.extensions.isVisible
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

    override var isInfoWaitForLocationVisible: Boolean = true
        set(value) {
            infoWaitForLocation?.isVisible = value
            field = value
        }

    override var isMapButtonInLockState: Boolean = true
        set(value) {

            mapLockButton?.let {

                val drawable: Drawable
                val back: Drawable

                if (value) {
                    drawable = getDrawable(R.drawable.ic_lock_24dp)
                    back = getDrawable(R.drawable.button_lock_back)
                } else {
                    drawable = getDrawable(R.drawable.ic_unlock_24dp)
                    back = getDrawable(R.drawable.button_unlock_back)
                }
                it.setImageDrawable(drawable)
                it.background = back
            }

            field = value
        }

    private val onMapAnLayoutReady = OnMapAndLayoutReady {

        infoContainerVisibility = View.VISIBLE
        adjustCameraToInfoContainer()
        setOnTouchInfoContainerExpandButton()
    }

    @SuppressLint("ShowToast")
    override fun onCreate(savedInstanceState: Bundle?) {

        //Dagger MUST be first
        AnotherBikeApp
                .get(this.application)
                .trackingComponent(this, getIBinderFromIntent())
                .inject(this)
        
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tracking)
        activateToolbar(toolbarMain)
        setResult(TrackingPresenter.Result.NOT_DONE)

        unfreezeInstanceState(savedInstanceState)
        savedInstanceState?.let {

            isMapButtonInLockState = (it[LOCK_BUTTON_STATE_KEY] as Boolean?) ?: isMapButtonInLockState
        }

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

        val bundle = intent.getBundleExtra(MAIN_BUNDLE_KEY)
        bundle ?: throw IllegalStateException("No bundle")
        return bundle.getBinder(BINDER_KEY) as TrackingService.ServiceBinder
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
            uiSettings.isMyLocationButtonEnabled = false
            isMyLocationEnabled = true
            setMapStyle(MapStyleOptions(getGoogleMapStyleFromTheme()))
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
        outState.putBoolean(LOCK_BUTTON_STATE_KEY, isMapButtonInLockState)
    }

    override fun onDestroy() {
        super.onDestroy()

        Timber.v("Tracking activity destroyed!")
        trackingPresenter.notifyOnDestroy(isFinishing)
    }

    // menu onClicks

    fun onClickFinishTracking(@Suppress("UNUSED_PARAMETER") menuItem: MenuItem?) {

        trackingPresenter.onClickFinishTracking()
    }

    fun onClickLockMap(@Suppress("UNUSED_PARAMETER") view: View) {

        trackingPresenter.onClickLockMap()
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

    override fun updateStats(statistics: Map<Statistic.Name, Statistic<*>>) {
        updateShortStats(statistics)
        updateInfoFragmentStats(statistics)
    }

    private fun updateShortStats(stats: Map<Statistic.Name, Statistic<*>>) {

        if (emptyShortStatsText != null) {

            initShortStats(stats)

        } else {

            updateExistingShortStats(stats)
        }
    }

    private fun initShortStats(stats: Map<Statistic.Name, Statistic<*>>) {

        (shortStatsContainer as ViewGroup).removeAllViews()

        for (statName in shortStatsListToDisplay) {

            (shortStatsContainer as ViewGroup).addView(ViewExtensions.createTextViewStatWithTag(
                    layoutInflater,
                    (shortStatsContainer as ViewGroup),
                    R.layout.activity_tracking_short_stat,
                    statName,
                    stringsResolver.resolve(statName, stats[statName]!!)
            ))
        }

    }

    private fun updateExistingShortStats(stats: Map<Statistic.Name, Statistic<*>>) {

        for (statName in shortStatsListToDisplay) {

            shortStatsContainer.findViewWithTag<TextView>(statName).text =
                    stringsResolver.resolve(statName, stats[statName]!!)
        }
    }

    private fun updateInfoFragmentStats(stats: Map<Statistic.Name, Statistic<*>>) {

        (supportFragmentManager.findFragmentById(R.id.statsFragment) as StatsFragment?)
                ?.updateStats(stats)
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
                .setMessage(R.string.messageFinishTracking)
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
        private const val MAIN_BUNDLE_KEY = "main bundle"
        private const val BINDER_KEY = "IBinder"
        private const val LOCK_BUTTON_STATE_KEY = "lockButtonState"

        fun newIntent(context: Context, serviceBinder: TrackingService.ServiceBinder) =
                Intent(context, TrackingActivity::class.java).apply {

                    val bundle = Bundle()
                    bundle.putBinder(BINDER_KEY, serviceBinder)
                    putExtra(MAIN_BUNDLE_KEY, bundle)
                }
    }

}
