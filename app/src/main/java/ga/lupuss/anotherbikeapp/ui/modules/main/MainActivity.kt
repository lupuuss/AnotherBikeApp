package ga.lupuss.anotherbikeapp.ui.modules.main

import android.animation.Animator
import android.animation.AnimatorInflater
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.view.GravityCompat
import android.support.v4.widget.NestedScrollView
import android.view.View
import ga.lupuss.anotherbikeapp.AnotherBikeApp

import ga.lupuss.anotherbikeapp.R
import ga.lupuss.anotherbikeapp.base.ThemedActivity
import ga.lupuss.anotherbikeapp.ui.modules.summary.SummaryActivity
import ga.lupuss.anotherbikeapp.ui.modules.tracking.TrackingActivity

import timber.log.Timber
import javax.inject.Inject
import android.support.v7.app.AlertDialog
import android.widget.AdapterView
import android.widget.TextView
import ga.lupuss.anotherbikeapp.models.android.AndroidTrackingServiceGovernor
import ga.lupuss.anotherbikeapp.models.base.TrackingServiceGovernor
import ga.lupuss.anotherbikeapp.ui.adapters.DrawerListViewAdapter
import ga.lupuss.anotherbikeapp.ui.extensions.addOnAnimationEndListener
import ga.lupuss.anotherbikeapp.ui.modules.login.LoginActivity
import ga.lupuss.anotherbikeapp.ui.modules.routeshistory.RoutesHistoryFragment
import ga.lupuss.anotherbikeapp.ui.modules.settings.SettingsActivity
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main_ui.*


/**
 * Main user's interface.
 */
class MainActivity
    : ThemedActivity(),
        MainView,
        AdapterView.OnItemClickListener {

    enum class ItemName {
        SIGN_OUT, SETTINGS
    }

    class StrIconRes(
            val str: Int,
            val icon: Int
    )

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

    private val drawerListViewChildren = listOf(
            Pair(ItemName.SIGN_OUT, StrIconRes(R.string.signOut, R.drawable.ic_sign_out_24dp)),
            Pair(ItemName.SETTINGS, StrIconRes(R.string.settings, R.drawable.ic_settings_24dp))
    )

    override var isDrawerLayoutOpened = false
        get() = drawerLayout?.isDrawerOpen(GravityCompat.START) ?: false
        private set

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        mainPresenter.notifyOnResult(requestCode, resultCode)
    }

    override fun onNewIntent(intent: Intent?) {
        intent?.let {

            mainPresenter.notifyOnResult(
                    it.extras?.get(MainActivity.REQUEST_CODE_KEY) as? Int ?: -1,
                    0
            )
        }
    }

    @SuppressLint("ShowToast")
    override fun onCreate(savedInstanceState: Bundle?) {

        requiresVerification()

        super.onCreate(savedInstanceState)
    }

    override fun onCreatePostVerification(savedInstanceState: Bundle?) {

        // Dagger MUST be first
        // super method requires it

        AnotherBikeApp.get(this.application)
                .mainComponent(this)
                .inject(this)

        super.onCreatePostVerification(savedInstanceState)

        setContentView(R.layout.activity_main)
        androidTrackingServiceGovernor.init(this, savedInstanceState)

        activateToolbar(toolbarMain, drawerLayout)

        drawerListView.adapter = DrawerListViewAdapter(drawerListViewChildren, layoutInflater)
        drawerListView.onItemClickListener = this

        mainScroll.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, _, _, _, _ ->

            if (v.canScrollVertically(1)) {

                (supportFragmentManager
                        .findFragmentById(R.id.routesHistoryFragment) as RoutesHistoryFragment)
                        .notifyParentScrollReachedBottom()
            }
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
        Timber.v("MainActivity destroyed!")
    }

    override fun onDestroyPostVerification() {
        super.onDestroyPostVerification()
        mainPresenter.notifyOnDestroy(isFinishing)
        androidTrackingServiceGovernor.destroy(isFinishing)
    }

    // onClicks

    // delegates to onClickTrackingButtonHelper to avoid "incorrect method signature" in XML onClick
    fun onClickTrackingButton(view: View) {

        onClickTrackingButtonHelper(view)
    }

    fun onClickTrackingButtonHelper(view: View): Animator {

        val animator = AnimatorInflater.loadAnimator(this, R.animator.tracking_button)
        animator.setTarget(view)
        animator.addOnAnimationEndListener {
            mainPresenter.onClickTrackingButton()
        }

        animator.start()
        return animator
    }

    // Drawer Layout
    override fun onItemClick(adapterView: AdapterView<*>, view: View?, i: Int, l: Long) {

        val item = adapterView.adapter.getItem(i)

        if (item is Pair<*, *> && item.first is ItemName) {

            @Suppress("UNCHECKED_CAST")
            when (item.first) {

                ItemName.SIGN_OUT -> mainPresenter.onClickSignOut()
                ItemName.SETTINGS -> mainPresenter.onClickSettings()
            }
        }

    }

    // MainView Impl

    override fun hideDrawer() {

        drawerLayout.closeDrawer(GravityCompat.START)
    }

    override fun setTrackingButtonState(trackingInProgress: Boolean) {

        trackingButton.setText(
                if (trackingInProgress) R.string.continueTracking else R.string.startTracking
        )
    }

    override fun setDrawerHeaderInfo(displayName: String?, email: String?) {

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
                MainPresenter.Request.TRACKING_ACTIVITY_REQUEST
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

    companion object {

        const val REQUEST_CODE_KEY = "requestCodeKey"

        @JvmStatic
        fun newIntent(context: Context) = Intent(context, MainActivity::class.java)
    }
}
