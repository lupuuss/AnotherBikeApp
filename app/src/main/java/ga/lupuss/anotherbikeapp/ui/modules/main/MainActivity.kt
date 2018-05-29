package ga.lupuss.anotherbikeapp.ui.modules.main

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorInflater
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.PermissionChecker
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import ga.lupuss.anotherbikeapp.AnotherBikeApp

import ga.lupuss.anotherbikeapp.R
import ga.lupuss.anotherbikeapp.base.BaseActivity
import ga.lupuss.anotherbikeapp.dpToPixels
import ga.lupuss.anotherbikeapp.models.trackingservice.TrackingService
import ga.lupuss.anotherbikeapp.ui.adapters.RoutesHistoryRecyclerViewAdapter
import ga.lupuss.anotherbikeapp.ui.decorations.BottomSpaceItemDecoration
import ga.lupuss.anotherbikeapp.ui.modules.summary.SummaryActivity
import ga.lupuss.anotherbikeapp.ui.modules.tracking.TrackingActivity

import timber.log.Timber
import javax.inject.Inject
import android.support.v4.widget.NestedScrollView
import android.widget.TextView
import ga.lupuss.anotherbikeapp.models.User
import ga.lupuss.anotherbikeapp.ui.adapters.IconStringListViewAdapter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main_ui.*


/**
 * Main user's interface.
 */
class MainActivity : BaseActivity(), MainView {

    @Inject
    lateinit var user: User

    @Inject
    lateinit var mainPresenter: MainPresenter

    private val locationPermissionRequestCode = 1
    private var onLocationPermissionRequestResult: ((Boolean) -> Unit)? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == Request.TRACKING_ACTIVITY_REQUEST) {

            mainPresenter.notifyOnResult(requestCode, resultCode)
        }

    }

    @SuppressLint("ShowToast")
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        DaggerMainComponent.builder()
                .anotherBikeAppComponent(AnotherBikeApp.get(this.application).mainComponent!!)
                .mainModule(MainModule(this))
                .build()
                .inject(this)

        mainPresenter.notifyOnCreate(savedInstanceState)

        drawerListView.adapter = IconStringListViewAdapter(this, layoutInflater)
        drawerListView.addHeaderView(

                layoutInflater.inflate(
                        R.layout.activity_main_drawer_header,
                        drawerListView,
                        false
                ).apply {

                    this.findViewById<TextView>(R.id.userName).text = user.name
                    this.findViewById<TextView>(R.id.userEmail).text = user.firebaseUser.email
                }
        )

        val adapter = RoutesHistoryRecyclerViewAdapter(
                mainPresenter::onHistoryRecyclerItemRequest,
                mainPresenter::onHistoryRecyclerItemCountRequest
        )

        routesHistoryRecycler.setItemViewCacheSize(3)
        routesHistoryRecycler.isNestedScrollingEnabled = false
        routesHistoryRecycler.apply {
            this.adapter = adapter
            layoutManager = LinearLayoutManager(this@MainActivity)
        }

        recyclerWrapper.setOnScrollChangeListener(
                { v: NestedScrollView?, _, _, _, _ ->

                    if (!v!!.canScrollVertically(1)) {

                        mainPresenter.notifyRecyclerReachedBottom()
                    }
                })

        routesHistoryRecycler.addItemDecoration(
                BottomSpaceItemDecoration(dpToPixels(this, 5F)))
    }

    override fun onSaveInstanceState(outState: Bundle?) {

        super.onSaveInstanceState(outState)
        mainPresenter.notifyOnSavedInstanceState(outState!!)
    }

    override fun onBackPressed() {
        finishFromChild(this.parent)
        finishAndRemoveTask()
    }

    override fun onDestroy() {

        super.onDestroy()
        mainPresenter.notifyOnDestroy(isFinishing)
        Timber.d("MainActivity destroyed!")
    }

    // onClicks

    fun onClickTrackingButton(view: View) {

        val animator = AnimatorInflater.loadAnimator(this, R.animator.tracking_button)
        animator.setTarget(view)
        animator.addListener(object : Animator.AnimatorListener {

            override fun onAnimationRepeat(p0: Animator?) = Unit

            override fun onAnimationCancel(p0: Animator?) = Unit

            override fun onAnimationStart(p0: Animator?) = Unit

            override fun onAnimationEnd(p0: Animator?) {

                mainPresenter.onClickTrackingButton()
            }

        })

        animator.start()
    }

    // MainView Impl

    override fun setNoDataTextVisibility(visibility: Int) {

        noDataText.visibility = visibility
    }

    override fun setTrackingButtonState(trackingInProgress: Boolean) {

        trackingButton.setText(
                if (trackingInProgress) R.string.continue_tracking else R.string.start_tracking
        )
    }

    override fun setRoutesHistoryVisibility(visibility: Int) {

        routesHistoryRecycler.visibility = visibility
    }

    override fun startTrackingActivity(serviceBinder: TrackingService.ServiceBinder?) {

        startActivityForResult(
                TrackingActivity.newIntent(this@MainActivity, serviceBinder!!),
                Request.TRACKING_ACTIVITY_REQUEST
        )
    }

    override fun requestLocationPermission(onLocationPermissionRequestResult: (Boolean) -> Unit) {

        ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), locationPermissionRequestCode)

        this.onLocationPermissionRequestResult = onLocationPermissionRequestResult
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == locationPermissionRequestCode) {

            onLocationPermissionRequestResult
                    ?.invoke(grantResults[0] == PermissionChecker.PERMISSION_GRANTED)
        }

    }

    override fun startTrackingService() {

        // after bind onServiceStart is called by serviceConnection callback

        Timber.d("Starting service...")
        startService(Intent(this, TrackingService::class.java))
    }

    override fun bindTrackingService(connection: ServiceConnection) {

        Timber.d("Binding service...")
        bindService(
                Intent(this, TrackingService::class.java),
                connection,
                Context.BIND_AUTO_CREATE
        )
    }

    override fun stopTrackingService() {

        Timber.d("Stopping service...")
        stopService(Intent(this, TrackingService::class.java))
    }

    override fun unbindTrackingService(connection: ServiceConnection) {

        Timber.d("Unbinding service...")
        unbindService(connection)
    }

    override fun startSummaryActivity() {

        startActivity(SummaryActivity.newIntent(this))
    }

    override fun setProgressBarVisibility(visibility: Int) {
        recyclerProgressBar.visibility = visibility
    }

    override fun refreshRecyclerAdapter() {
        routesHistoryRecycler.adapter.notifyDataSetChanged()
    }

    override fun notifyRecyclerItemChanged(position: Int) {
        routesHistoryRecycler.adapter.notifyItemChanged(position)
    }

    override fun notifyRecyclerItemRemoved(position: Int) {
        routesHistoryRecycler.adapter.notifyItemRemoved(position)
    }

    override fun notifyRecyclerItemInserted(position: Int) {
        routesHistoryRecycler.adapter.notifyItemInserted(position)
    }

    class Request {
        companion object {
            const val TRACKING_ACTIVITY_REQUEST = 0
        }
    }

    companion object {
        @JvmStatic
        fun newIntent(context: Context) = Intent(context, MainActivity::class.java)
    }
}
