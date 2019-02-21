package ga.lupuss.anotherbikeapp.ui.modules.summary

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.os.ConfigurationCompat
import android.support.v7.app.AlertDialog
import android.support.v7.widget.RecyclerView
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.ViewTreeObserver
import android.widget.TextView
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.squareup.picasso.Picasso
import ga.lupuss.anotherbikeapp.AnotherBikeApp
import ga.lupuss.anotherbikeapp.R
import ga.lupuss.anotherbikeapp.models.dataclass.Statistic
import ga.lupuss.anotherbikeapp.ui.extensions.ViewExtensions
import ga.lupuss.anotherbikeapp.ui.extensions.fitToPoints
import ga.lupuss.anotherbikeapp.ui.extensions.getColorForAttr
import ga.lupuss.anotherbikeapp.ui.extensions.isVisible
import ga.lupuss.anotherbikeapp.ui.fragments.StatsFragment
import kotlinx.android.synthetic.main.activity_summary.*
import javax.inject.Inject
import ga.lupuss.anotherbikeapp.base.StatsActivity
import ga.lupuss.anotherbikeapp.dpToPixels
import ga.lupuss.anotherbikeapp.models.base.PathsGenerator
import ga.lupuss.anotherbikeapp.models.dataclass.RoutePhoto
import ga.lupuss.anotherbikeapp.ui.adapters.RoutePhotosRecyclerViewAdapter
import ga.lupuss.anotherbikeapp.ui.modules.tracking.OnMapAndLayoutReady

class SummaryActivity
    : StatsActivity(), SummaryView,
        OnMapReadyCallback, TextWatcher,
        OnMapAndLayoutReady.Listener, ViewTreeObserver.OnGlobalLayoutListener {


    @Inject
    lateinit var summaryPresenter: MainSummaryPresenter

    @Inject
    lateinit var picasso: Picasso

    @Inject
    lateinit var pathsGenerator: PathsGenerator

    private lateinit var mode: SummaryPresenter.Mode
    private lateinit var rejectItem: MenuItem
    private lateinit var saveItem: MenuItem

    override var isRouteEditLineVisible: Boolean = true
        set(value) {
            nameLabel?.let {
                it.isVisible = value
                routeNameEdit.isVisible = value
            }
            field = value
        }

    override var isStatsFragmentVisible: Boolean = true
        set(value) {

            routeInfoContainer?.isVisible = value
            field = value
        }

    override var isProgressBarVisible: Boolean = false
        set(value) {

            summaryProgressBar?.let {
                it.isVisible = value
            }
            field = value
        }

    override var isRejectActionVisible = true
        set(value) {

            if (::rejectItem.isInitialized) {

                rejectItem.isVisible = value
            }
            field = value
        }

    override var isSaveActionVisible = true
        set(value) {

            if (::saveItem.isInitialized) {

                saveItem.isVisible= value
            }
            field = value
        }

    override lateinit var photosAdapter: RecyclerView.Adapter<*>

    private var routePhotoCallback: (Int) -> RoutePhoto = { RoutePhoto("", "", 0) }
    private var routePhotosSizeCallback: () -> Int = { 0 }

    private val onMapAndLayoutReady = OnMapAndLayoutReady(this)

    override fun setPhotosAdaptersCallbacks(routePhotoCallback: (Int) -> RoutePhoto,
                                            routePhotosSizeCallback: () -> Int) {

        this.routePhotoCallback = routePhotoCallback
        this.routePhotosSizeCallback = routePhotosSizeCallback
        photosAdapter.notifyDataSetChanged()
    }

    override fun onNewPhotoTaken(photo: RoutePhoto) {
        // ignore
    }

    override fun onClickDeletePhoto(position: Int) {

    }

    override fun onCreate(savedInstanceState: Bundle?) {

        requiresVerification()
        super.onCreate(savedInstanceState)
    }

    override fun onCreatePostVerification(savedInstanceState: Bundle?) {

        // Dagger MUST be first
        // super method requires it

        AnotherBikeApp.get(application)
                .summaryComponent(this)
                .inject(this)

        super.onCreatePostVerification(savedInstanceState)

        setContentView(R.layout.activity_summary)
        activateToolbar(toolbarSummary)

        initInfoViewPager()
        routePhotosFragment.isTakingNewPhotoEnabled = false

        photosAdapter = RoutePhotosRecyclerViewAdapter(
                picasso,
                { routePhotoCallback(it) },
                { routePhotosSizeCallback() },
                ConfigurationCompat.getLocales(resources.configuration)[0]!!,
                this::onClickDeletePhoto,
                pathsGenerator
        )

        mode = SummaryPresenter.Mode.valueOf(intent.extras.getString(MODE_KEY))

        var docReference: String? = null

        if (mode == SummaryPresenter.Mode.OVERVIEW) {

            docReference = intent.extras.getString(DOC_REFERENCE_KEY)
        }

        summaryPresenter.initMode(mode, docReference)

        routeNameEdit.addTextChangedListener(this)
        (supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment).getMapAsync(this)
        routeInfoContainer.viewTreeObserver.addOnGlobalLayoutListener(this)
    }

    override fun onGlobalLayout() {
        onMapAndLayoutReady.layoutReady()
        routeInfoContainer.viewTreeObserver.removeOnGlobalLayoutListener(this)
    }

    override fun onMapReadyPostVerification(googleMap: GoogleMap?) {
        super.onMapReadyPostVerification(googleMap)
        onMapAndLayoutReady.mapReady()
    }

    override fun onMapAndLayoutReady() {

        summaryPresenter.notifyOnViewReady()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.activity_summary_menu, menu)

        rejectItem = menu!!.findItem(R.id.itemRejectRoute)
        saveItem = menu.findItem(R.id.itemSaveRoute)

        saveItem.isVisible = isSaveActionVisible
        rejectItem.isVisible = isRejectActionVisible

        return true
    }

    override fun onBackPressed() {

        summaryPresenter.onExitRequest()
    }

    override fun onDestroyPostVerification() {
        super.onDestroyPostVerification()
        summaryPresenter.notifyOnDestroy(isFinishing)
    }

    override fun afterTextChanged(p0: Editable?) {}

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        summaryPresenter.onNameEditTextChanged(p0)
    }

    // onClicks

    @Suppress("UNUSED_PARAMETER")
    fun onSaveClick(menuItem: MenuItem?) {

        hideSoftKeyboard()
        summaryPresenter.onSaveClick()
    }

    @Suppress("UNUSED_PARAMETER")
    fun onRejectClick(menuItem: MenuItem?) {

        hideSoftKeyboard()
        summaryPresenter.onRejectClick()
    }

    // SummaryView impl

    override fun setNameLabelValue(value: String) {
        routeNameEdit.setText(value, TextView.BufferType.EDITABLE)
    }

    override fun showRouteLine(points: List<LatLng>) {

        val icon = ViewExtensions.getDefaultMarkerIconForColor(theme.getColorForAttr(R.attr.markersColor))

        if (points.isNotEmpty()) {

            map.addMarker(
                    MarkerOptions()
                            .title(getString(R.string.start))
                            .icon(icon)
                            .position(points.first())
            )

            map.addPolyline(PolylineOptions().addAll(points).color(theme.getColorForAttr(R.attr.trackLineColor)))

            map.addMarker(
                    MarkerOptions()
                            .title(getString(R.string.end))
                            .icon(icon)
                            .position(points.last())
            )

            map.fitToPoints(points, 150, 18F, resources.displayMetrics.widthPixels, dpToPixels(this, 350F))
        }
    }

    override fun showStatistics(statistics: Map<Statistic.Name, Statistic<*>>) {

        statsFragment.updateStats(statistics, StatsFragment.Mode.SUMMARY_STATS)
    }

    override fun showRejectDialog(onYes: () -> Unit) {

        AlertDialog.Builder(this)
                .setIcon(R.drawable.ic_delete_24dp)
                .setTitle(R.string.warning)
                .setMessage(R.string.messageRejectWarning)
                .setPositiveButton(R.string.reject, { _, _ -> onYes.invoke() })
                .setNegativeButton(R.string.cancel, null)
                .show()
    }

    override fun showDeleteDialog(onYes: () -> Unit) {
        AlertDialog.Builder(this)
                .setIcon(R.drawable.ic_delete_24dp)
                .setTitle(R.string.warning)
                .setMessage(R.string.messageDeleteRouteWarning)
                .setPositiveButton(R.string.delete, { _, _ -> onYes.invoke() })
                .setNegativeButton(R.string.cancel, null)
                .show()
    }

    override fun showUnsavedStateDialog(onYes: () -> Unit) {

        AlertDialog.Builder(this)
                .setIcon(R.drawable.ic_delete_24dp)
                .setTitle(R.string.warning)
                .setMessage(R.string.messageUnsavedState)
                .setPositiveButton(R.string.exit, { _, _ -> onYes.invoke() })
                .setNegativeButton(R.string.cancel, null)
                .show()
    }

    override fun getRouteNameFromEditText(): String = routeNameEdit.text.toString()

    companion object {

        const val MODE_KEY = "modeKey"
        const val DOC_REFERENCE_KEY = "positionKey"

        @JvmStatic
        fun newIntent(context: Context): Intent =
                Intent(context, SummaryActivity::class.java)
                        .putExtra(MODE_KEY, SummaryPresenter.Mode.AFTER_TRACKING_SUMMARY.toString())

        @JvmStatic
        fun newIntent(context: Context, docReference: String): Intent =
                Intent(context, SummaryActivity::class.java)
                        .putExtra(MODE_KEY, SummaryPresenter.Mode.OVERVIEW.toString())
                        .putExtra(DOC_REFERENCE_KEY, docReference)

    }
}
