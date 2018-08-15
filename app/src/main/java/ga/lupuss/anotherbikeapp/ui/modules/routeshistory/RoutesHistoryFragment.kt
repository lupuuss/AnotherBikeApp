package ga.lupuss.anotherbikeapp.ui.modules.routeshistory


import android.content.Context
import android.os.Bundle
import android.support.v4.widget.NestedScrollView
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import ga.lupuss.anotherbikeapp.AnotherBikeApp

import ga.lupuss.anotherbikeapp.R
import ga.lupuss.anotherbikeapp.di.BaseFragment
import ga.lupuss.anotherbikeapp.dpToPixels
import ga.lupuss.anotherbikeapp.ui.adapters.RoutesHistoryRecyclerViewAdapter
import ga.lupuss.anotherbikeapp.ui.decorations.BottomSpaceItemDecoration
import ga.lupuss.anotherbikeapp.ui.extensions.isGone
import ga.lupuss.anotherbikeapp.ui.extensions.isVisible
import ga.lupuss.anotherbikeapp.ui.modules.summary.SummaryActivity
import kotlinx.android.synthetic.main.fragment_routes_history.*

import javax.inject.Inject

class RoutesHistoryFragment
    : BaseFragment(),
        RoutesHistoryView,
        RoutesHistoryRecyclerViewAdapter.OnItemClickListener  {

    @Inject
    lateinit var routesHistoryPresenter: RoutesHistoryPresenter

    override var isNoDataTextVisible: Boolean = false
        set(value){ noDataText?.isVisible = value }

    override var isRoutesHistoryVisible: Boolean = true
        set(value) { routesHistoryRecycler?.isVisible = value }

    override var isProgressBarVisible: Boolean = true
        set(value) { recyclerProgressBar?.isGone = !value }

    override fun onAttach(context: Context?) {
        super.onAttach(context)

        AnotherBikeApp
                .get(requireActivity().application)
                .routesHistoryComponent(this)
                .inject(this)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_routes_history, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val recyclerViewAdapter = RoutesHistoryRecyclerViewAdapter(
                routesHistoryPresenter::onHistoryRecyclerItemRequest,
                routesHistoryPresenter::onHistoryRecyclerItemCountRequest,
                routesHistoryPresenter::speedUnit,
                routesHistoryPresenter::distanceUnit,
                stringsResolver
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

        recyclerWrapper.setOnScrollChangeListener({ v: NestedScrollView?, _, _, _, _ ->

            if (!v!!.canScrollVertically(1))
                routesHistoryPresenter.notifyRecyclerReachedBottom()
        })

        routesHistoryPresenter.notifyOnViewReady()
    }

    override fun onDestroyView() {

        routesHistoryPresenter.notifyOnDestroy(true)
        super.onDestroyView()
    }

    // Recycler View
    override fun onItemClick(position: Int) {

        routesHistoryPresenter.onClickShortRoute(position)
    }

    override fun refreshRecyclerAdapter() {
        routesHistoryRecycler.adapter.notifyDataSetChanged()
    }

    override fun notifyRecyclerItemChanged(position: Int) {

        routesHistoryRecycler.adapter.notifyItemChanged(position)
    }

    override fun notifyRecyclerItemRemoved(position: Int, size: Int) {
        routesHistoryRecycler.adapter.notifyItemRemoved(position)
        routesHistoryRecycler.adapter.notifyItemRangeChanged(0, size)
    }

    override fun notifyRecyclerItemInserted(position: Int, size: Int) {
        routesHistoryRecycler.adapter.notifyItemInserted(position)
        routesHistoryRecycler.adapter.notifyItemRangeChanged(0, size)
    }

    override fun startSummaryActivity(docRef: String) {

        requireActivity().startActivity(SummaryActivity.newIntent(this.requireContext(), docRef))
    }
}
