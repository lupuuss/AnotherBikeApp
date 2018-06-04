package ga.lupuss.anotherbikeapp.ui.modules.summary

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import ga.lupuss.anotherbikeapp.AnotherBikeApp
import ga.lupuss.anotherbikeapp.R
import ga.lupuss.anotherbikeapp.base.BaseActivity
import ga.lupuss.anotherbikeapp.models.dataclass.Statistic
import ga.lupuss.anotherbikeapp.ui.extensions.ViewExtensions
import ga.lupuss.anotherbikeapp.ui.extensions.fitToPoints
import ga.lupuss.anotherbikeapp.ui.extensions.getColorForAttr
import ga.lupuss.anotherbikeapp.ui.extensions.isVisible
import ga.lupuss.anotherbikeapp.ui.fragments.StatsFragment
import kotlinx.android.synthetic.main.activity_summary.*
import javax.inject.Inject

class SummaryActivity : BaseActivity(), SummaryView, OnMapReadyCallback {

    @Inject
    lateinit var summaryPresenter: MainSummaryPresenter

    lateinit var map: GoogleMap

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

            statsFragmentWrapper?.isVisible = value
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

    override fun onCreate(savedInstanceState: Bundle?) {

        // Dagger MUST be first
        DaggerSummaryComponent
                .builder()
                .anotherBikeAppComponent(AnotherBikeApp.get(this.application).anotherBikeAppComponent)
                .summaryModule(SummaryModule(this))
                .build()
                .inject(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_summary)
        activateToolbar(toolbarSummary)

        mode = SummaryPresenter.Mode.valueOf(intent.extras.getString(MODE_KEY))

        var docReference: String? = null

        if (mode == SummaryPresenter.Mode.OVERVIEW) {

            docReference = intent.extras.getString(DOC_REFERENCE_KEY)
        }

        summaryPresenter.initMode(mode, docReference)

        (supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment).getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap?) {

        this.map = map!!
        map.uiSettings.setAllGesturesEnabled(false)

        map.setOnMapClickListener {
            summaryPresenter.onMapClick()
        }
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

    override fun onDestroy() {
        super.onDestroy()
        summaryPresenter.notifyOnDestroy(isFinishing)
    }

    // onClicks

    @Suppress("UNUSED_PARAMETER")
    fun onSaveClick(menuItem: MenuItem?) {

        summaryPresenter.onSaveClick()
    }

    @Suppress("UNUSED_PARAMETER")
    fun onRejectClick(menuItem: MenuItem?) {

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

            map.fitToPoints(points, 150, 18F)
        }
    }

    override fun showStatistics(statistics: Map<Statistic.Name, Statistic<*>>) {

        (supportFragmentManager.findFragmentById(R.id.statsFragment) as? StatsFragment)?.updateStats(statistics)
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
