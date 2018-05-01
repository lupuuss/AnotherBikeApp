package ga.lupuss.anotherbikeapp.ui.modules.summary

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.FileObserver
import android.support.v7.app.AlertDialog
import android.view.Menu
import android.view.MenuItem
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import ga.lupuss.anotherbikeapp.AnotherBikeApp
import ga.lupuss.anotherbikeapp.R
import ga.lupuss.anotherbikeapp.base.BaseActivity
import ga.lupuss.anotherbikeapp.models.trackingservice.statisticsmanager.statistics.Statistic
import ga.lupuss.anotherbikeapp.ui.extensions.ViewExtensions
import ga.lupuss.anotherbikeapp.ui.extensions.fitToPoints
import ga.lupuss.anotherbikeapp.ui.extensions.getColorForAttr
import ga.lupuss.anotherbikeapp.ui.fragments.StatsFragment
import kotlinx.android.synthetic.main.activity_summary.*
import javax.inject.Inject

class SummaryActivity : BaseActivity(), SummaryView, OnMapReadyCallback {

    @Inject
    lateinit var summaryPresenter: SummaryPresenter

    lateinit var map: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_summary)
        activateToolbar(toolbarSummary)

        DaggerSummaryComponent
                .builder()
                .anotherBikeAppComponent(AnotherBikeApp.get(this.application).mainComponent)
                .summaryModule(SummaryModule(this))
                .build()
                .inject(this)

        (supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment).getMapAsync(this)
    }

    override fun onMapReady(map: GoogleMap?) {

        this.map = map!!
        summaryPresenter.viewReady()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.activity_summary_menu, menu)
        return true
    }

    override fun onBackPressed() {

        onRejectClick(null)
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

    override fun showRouteLine(points: List<LatLng>) {

        val icon = ViewExtensions.getDefaultMarkerIconForColor(theme.getColorForAttr(R.attr.markersColor))

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
        map.uiSettings.setAllGesturesEnabled(false)

        map.setOnMapClickListener {
            summaryPresenter.onMapClick()
        }
    }

    override fun showStatistics(statistics: Map<Statistic.Name, Statistic>) {
        (supportFragmentManager.findFragmentById(R.id.statsFragment) as StatsFragment)
                .updateStats(statistics)
    }

    override fun finishActivity() {
        finish()
    }

    override fun showRejectDialog(onYes: () -> Unit) {

        AlertDialog.Builder(this)
                .setIcon(R.drawable.ic_delete_24dp)
                .setTitle(R.string.warning)
                .setMessage(R.string.message_reject_warning)
                .setPositiveButton(R.string.reject, { _, _ -> onYes.invoke() })
                .setNegativeButton(R.string.cancel, null)
                .show()
    }

    override fun getRouteNameFromEditText(): String = routeNameEdit.text.toString()

    companion object {

        @JvmStatic
        fun newIntent(context: Context) =
                Intent(context, SummaryActivity::class.java)
    }
}
