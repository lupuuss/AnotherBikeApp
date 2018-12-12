package ga.lupuss.anotherbikeapp.base

import ga.lupuss.anotherbikeapp.R
import ga.lupuss.anotherbikeapp.ui.adapters.RouteInfoPagerAdapter
import ga.lupuss.anotherbikeapp.ui.fragments.StatsFragment
import ga.lupuss.anotherbikeapp.ui.modules.routephotos.RoutePhotosFragment
import kotlinx.android.synthetic.main.route_info_container.*

abstract class StatsActivity : ThemedMapActivity(), RoutePhotosFragment.Listener {

    fun initInfoViewPager() {

        infoViewPager.adapter = RouteInfoPagerAdapter(
                this,
                supportFragmentManager,
                listOf(
                        R.drawable.ic_insert_chart_12dp to StatsFragment(),
                        R.drawable.ic_image_12dp to RoutePhotosFragment()
                )
        )

        infoTabLayout.setupWithViewPager(infoViewPager)
    }

    protected val statsFragment: StatsFragment
        get() = (infoViewPager.adapter as RouteInfoPagerAdapter)
                .getFragmentAt(0) as StatsFragment

    protected val routePhotosFragment: RoutePhotosFragment
        get() = (infoViewPager.adapter as RouteInfoPagerAdapter)
                .getFragmentAt(1) as RoutePhotosFragment
}