package ga.lupuss.anotherbikeapp.ui.adapters

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import android.view.ViewGroup
import android.util.SparseArray
import android.text.Spannable
import android.text.style.ImageSpan
import android.text.SpannableString



class RouteInfoPagerAdapter(
        private val context: Context,
        fm: FragmentManager,
        iconFragmentsList: List<Pair<Int, Fragment>>) : FragmentPagerAdapter(fm) {

    private val fragmentsList = mutableListOf<Fragment>()
    private val icons = mutableListOf<Int>()
    private val registeredFragments = SparseArray<Fragment>()

    init {

        iconFragmentsList.forEach { (icon, fragment) ->
            fragmentsList.add(fragment)
            icons.add(icon)
        }
    }

    override fun getItem(position: Int): Fragment {
        return fragmentsList[position]
    }

    override fun getCount(): Int {
        return fragmentsList.size
    }

    fun getFragmentAt(position: Int): Fragment {

        return if (registeredFragments[position] == null) {
            fragmentsList[position]
        } else {
            registeredFragments[position]
        }
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val fragment = super.instantiateItem(container, position) as Fragment
        registeredFragments.put(position, fragment)
        return fragment
    }

    override fun getPageTitle(position: Int): CharSequence? {

        val image = context.getDrawable(icons[position])!!
        image.setBounds(0, 0, image.intrinsicWidth, image.intrinsicHeight)
        val sb = SpannableString(" ")
        val imageSpan = ImageSpan(image, ImageSpan.ALIGN_BOTTOM)
        sb.setSpan(imageSpan, 0, 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        return sb
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        registeredFragments.remove(position)
        super.destroyItem(container, position, `object`)
    }
}