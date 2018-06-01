package ga.lupuss.anotherbikeapp.ui.modules.main

import android.animation.Animator
import android.animation.AnimatorInflater
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import ga.lupuss.anotherbikeapp.AnotherBikeApp

import ga.lupuss.anotherbikeapp.R
import ga.lupuss.anotherbikeapp.base.BaseActivity
import ga.lupuss.anotherbikeapp.dpToPixels
import ga.lupuss.anotherbikeapp.ui.adapters.RoutesHistoryRecyclerViewAdapter
import ga.lupuss.anotherbikeapp.ui.decorations.BottomSpaceItemDecoration
import ga.lupuss.anotherbikeapp.ui.modules.summary.SummaryActivity
import ga.lupuss.anotherbikeapp.ui.modules.tracking.TrackingActivity

import timber.log.Timber
import javax.inject.Inject
import android.support.v4.widget.NestedScrollView
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AlertDialog
import android.widget.AdapterView
import android.widget.TextView
import ga.lupuss.anotherbikeapp.models.android.AndroidTrackingServiceGovernor
import ga.lupuss.anotherbikeapp.models.interfaces.TrackingServiceGovernor
import ga.lupuss.anotherbikeapp.ui.adapters.DrawerListViewAdapter
import ga.lupuss.anotherbikeapp.ui.modules.login.LoginActivity
import ga.lupuss.anotherbikeapp.ui.modules.settings.SettingsActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main_ui.*


/**
 * Main user's interface.
 */
class MainActivity
    : BaseActivity(),
        MainView,
        AdapterView.OnItemClickListener,
        RoutesHistoryRecyclerViewAdapter.OnItemClickListener {

    @Inject
    lateinit var mainPresenter: MainPresenter

    /**
     * [trackingServiceGovernor] is just for proper dependency injection.
     * In fact it is [AndroidTrackingServiceGovernor] so [androidTrackingServiceGovernor]
     * should be used inside Activity
     */
    @Inject
    lateinit var trackingServiceGovernor: TrackingServiceGovernor

    private val androidTrackingServiceGovernor
        get() = trackingServiceGovernor as AndroidTrackingServiceGovernor

    enum class ItemName {
        SIGN_OUT, SETTINGS
    }

    class StrIconRes(
            val str: Int,
            val icon: Int
    )

    private val drawerListViewChildren = listOf(
            Pair(ItemName.SIGN_OUT, StrIconRes(R.string.sign_out, R.drawable.ic_sign_out_24dp)),
            Pair(ItemName.SETTINGS, StrIconRes(R.string.settings, R.drawable.ic_settings_24dp))
    )

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == MainActivity.Request.TRACKING_ACTIVITY_REQUEST) {

            mainPresenter.notifyOnResult(requestCode, resultCode)
        }

    }

    @SuppressLint("ShowToast")
    override fun onCreate(savedInstanceState: Bundle?) {

        // Dagger MUST be first
        DaggerMainComponent.builder()
                .anotherBikeAppComponent(AnotherBikeApp.get(this.application).anotherBikeAppComponent)
                .mainModule(MainModule(this, AndroidTrackingServiceGovernor()))
                .build()
                .inject(this)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        activateToolbar(toolbarMain)

        androidTrackingServiceGovernor.init(this, savedInstanceState)

        drawerLayout.addDrawerListener(
                ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
                        .apply { syncState() }
        )

        drawerListView.adapter = DrawerListViewAdapter(drawerListViewChildren, layoutInflater)

        drawerListView.onItemClickListener = this

        val adapter = RoutesHistoryRecyclerViewAdapter(
                mainPresenter::onHistoryRecyclerItemRequest,
                mainPresenter::onHistoryRecyclerItemCountRequest,
                mainPresenter::speedUnit,
                mainPresenter::distanceUnit,
                stringsResolver
        )

        adapter.setOnItemClickListener(this)

        routesHistoryRecycler.apply {
            setItemViewCacheSize(3)
            isNestedScrollingEnabled = false
            this.adapter = adapter
            layoutManager = LinearLayoutManager(this@MainActivity)
            addItemDecoration(BottomSpaceItemDecoration(dpToPixels(this@MainActivity, 5F)))
        }

        recyclerWrapper.setOnScrollChangeListener({ v: NestedScrollView?, _, _, _, _ ->

            if (!v!!.canScrollVertically(1))
                mainPresenter.notifyRecyclerReachedBottom()

        })

        mainPresenter.notifyOnViewReady()
    }

    override fun onSaveInstanceState(outState: Bundle?) {

        super.onSaveInstanceState(outState)
        androidTrackingServiceGovernor.saveInstanceState(outState)
    }

    override fun onBackPressed() {

        mainPresenter.onExitRequest()
    }

    override fun finishActivity() {

        finishFromChild(this.parent)
        finishAndRemoveTask()
    }

    override fun onDestroy() {

        super.onDestroy()
        mainPresenter.notifyOnDestroy(isFinishing)
        Timber.d("MainActivity destroyed!")
    }

    // onClicks

    // Recycler View
    override fun onItemClick(position: Int) {

        mainPresenter.onClickShortRoute(position)
    }

    // Drawer Layout
    override fun onItemClick(adapterView: AdapterView<*>, view: View?, i: Int, l: Long) {

        @Suppress("UNCHECKED_CAST")
        when ((adapterView.adapter.getItem(i) as Pair<ItemName, StrIconRes>).first) {

            ItemName.SIGN_OUT -> mainPresenter.onClickSignOut()
            ItemName.SETTINGS -> mainPresenter.onClickSettings()
        }

    }

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

    override fun setDrawerHeaderInfos(displayName: String?, email: String?) {

        drawerListView.addHeaderView(
                layoutInflater.inflate(
                        R.layout.activity_main_drawer_header,
                        drawerListView,
                        false
                ).apply {

                    this.findViewById<TextView>(R.id.userName).text =
                            if (displayName == null || displayName == "")
                                getString(R.string.user)
                            else displayName

                    this.findViewById<TextView>(R.id.userEmail).text = email
                }
        )
    }

    //      Navigation

    override fun showExitWarningDialog(onYesClick: () -> Unit) {

        AlertDialog.Builder(this)
                .setTitle(R.string.warning)
                .setMessage(R.string.dataLostWarning)
                .setPositiveButton(R.string.exit, { _, _ ->
                    onYesClick.invoke()
                })
                .setNegativeButton(R.string.cancel, null)
                .show()

    }

    override fun startTrackingActivity() {

        startActivityForResult(
                TrackingActivity.newIntent(this@MainActivity,
                        androidTrackingServiceGovernor.serviceBinder!!),
                Request.TRACKING_ACTIVITY_REQUEST
        )
    }

    override fun startLoginActivity() {

        startActivity(LoginActivity.newIntent(this))
    }

    override fun startSettingsActivity() {

        startActivity(SettingsActivity.newIntent(this))
    }

    override fun startSummaryActivity() {

        startActivity(SummaryActivity.newIntent(this))
    }

    override fun startSummaryActivity(docRef: String) {

        startActivity(SummaryActivity.newIntent(this, docRef))
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
