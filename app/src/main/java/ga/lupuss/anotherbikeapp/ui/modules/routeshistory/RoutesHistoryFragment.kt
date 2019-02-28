package ga.lupuss.anotherbikeapp.ui.modules.routeshistory


import android.content.Context
import android.os.Bundle
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ga.lupuss.anotherbikeapp.AnotherBikeApp

import ga.lupuss.anotherbikeapp.R
import ga.lupuss.anotherbikeapp.base.ItemsRecyclerViewAdapter
import ga.lupuss.anotherbikeapp.base.LabeledFragment
import ga.lupuss.anotherbikeapp.dpToPixels
import ga.lupuss.anotherbikeapp.ui.adapters.RoutesHistoryRecyclerViewAdapter
import ga.lupuss.anotherbikeapp.ui.decorations.BottomSpaceItemDecoration
import ga.lupuss.anotherbikeapp.ui.extensions.isGone
import ga.lupuss.anotherbikeapp.ui.extensions.isVisible
import ga.lupuss.anotherbikeapp.ui.modules.summary.SummaryActivity
import kotlinx.android.synthetic.main.fragment_routes_history.*

import javax.inject.Inject

class RoutesHistoryFragment
    : LabeledFragment(),
        RoutesHistoryView,
        ItemsRecyclerViewAdapter.OnItemClickListener  {

    @Inject
    lateinit var routesHistoryPresenter: RoutesHistoryPresenter

    override var isNoDataTextVisible: Boolean = false
        set(value){
            noDataText?.isVisible = value
            field = value
        }

    override var isRoutesHistoryVisible: Boolean = true
        set(value) {
            routesHistoryRecycler?.isVisible = value
            field = value
        }

    override var isRoutesHistoryProgressBarVisible: Boolean = true
        set(value) {
            recyclerProgressBar?.isGone = !value
            field = value
        }

    private var isFirstStart = true

    override fun onAttach(context: Context?) {

        requiresVerification()
        super.onAttach(context)
    }

    override fun onAttachPostVerification(context: Context?) {

        // Dagger MUST be first
        // super method requires it

        AnotherBikeApp
                .get(requireActivity().application)
                .routesHistoryComponent(this)
                .inject(this)

        super.onAttachPostVerification(context)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        val viewGroup = super.onCreateView(inflater, container, savedInstanceState) as ViewGroup
        attachChildLayoutToParent(
                inflater.inflate(R.layout.fragment_routes_history, viewGroup, false),
                viewGroup as ConstraintLayout
        )
        return viewGroup
    }

    override fun onViewCreatedPostVerification(view: View, savedInstanceState: Bundle?) {
        super.onViewCreatedPostVerification(view, savedInstanceState)

        setLabel(R.string.routesHistory)

        val recyclerViewAdapter = RoutesHistoryRecyclerViewAdapter(
                routesHistoryPresenter::onHistoryRecyclerItemRequest,
                routesHistoryPresenter::onHistoryRecyclerItemCountRequest,
                routesHistoryPresenter::speedUnit,
                routesHistoryPresenter::distanceUnit,
                resourceResolver
        )

        recyclerViewAdapter.setOnItemClickListener(this)

        routesHistoryRecycler.apply {
            setItemViewCacheSize(10)
            isNestedScrollingEnabled = false
            this.adapter = recyclerViewAdapter
            layoutManager = LinearLayoutManager(this@RoutesHistoryFragment.requireContext())
            addItemDecoration(
                    BottomSpaceItemDecoration(
                            dpToPixels(this@RoutesHistoryFragment.requireContext(), 5F)
                    )
            )
        }

        routesHistoryPresenter.notifyOnViewReady()
    }

    override fun onStartPostVerification() {
        super.onStartPostVerification()
        routesHistoryPresenter.notifyOnStart(isFirstStart)
    }

    override fun onStop() {
        super.onStop()

        isFirstStart = false
    }

    override fun onDestroyViewPostVerification() {
        super.onDestroyViewPostVerification()
        routesHistoryPresenter.notifyOnDestroy(true)
    }

    fun notifyParentScrollReachedBottom() {

        routesHistoryPresenter.notifyRecyclerReachedBottom()
    }

    override fun onClickRefreshButton() {
        super.onClickRefreshButton()

        routesHistoryPresenter.onClickRefreshButton()
    }

    // Recycler View
    override fun onItemClick(position: Int) {

        routesHistoryPresenter.onClickShortRoute(position)
    }

    override fun refreshRecyclerAdapter() {
        routesHistoryRecycler.adapter!!.notifyDataSetChanged()
    }

    override fun notifyRecyclerItemChanged(position: Int) {

        routesHistoryRecycler.adapter!!.notifyItemChanged(position)
    }

    override fun notifyRecyclerItemRemoved(position: Int, size: Int) {
        routesHistoryRecycler.adapter!!.notifyItemRemoved(position)
        routesHistoryRecycler.adapter!!.notifyItemRangeChanged(0, size)
    }

    override fun notifyRecyclerItemInserted(position: Int, size: Int) {
        routesHistoryRecycler.adapter!!.notifyItemInserted(position)
        routesHistoryRecycler.adapter!!.notifyItemRangeChanged(0, size)
    }

    override fun startSummaryActivity(documentReference: String) {

        requireActivity().startActivity(SummaryActivity.newIntent(this.requireContext(), documentReference))
    }
}
