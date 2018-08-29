package ga.lupuss.anotherbikeapp.ui.extensions

import android.animation.Animator
import android.annotation.SuppressLint
import android.support.v4.graphics.ColorUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import ga.lupuss.anotherbikeapp.R
import ga.lupuss.anotherbikeapp.models.dataclass.Statistic

var View.isVisible
    get() = visibility != View.VISIBLE
    set(value) {

        visibility = if (value) View.VISIBLE else View.INVISIBLE
    }

var View.isGone
    get() = visibility != View.GONE
    set (value) {

        visibility = if (value) View.GONE else View.VISIBLE
    }

fun Animator.addOnAnimationEndListener(onEnd: (() -> Unit)?) {

    this.addListener(object : Animator.AnimatorListener {

        override fun onAnimationRepeat(p0: Animator?) = Unit

        override fun onAnimationCancel(p0: Animator?) = Unit

        override fun onAnimationStart(p0: Animator?) = Unit

        override fun onAnimationEnd(p0: Animator?) {

            onEnd?.invoke()
        }

    })
}


@SuppressLint("SetTextI18n")
class ViewExtensions {

    /** Contains methods to create statistic's views. */
    companion object {

        /**
         *  Set formatted statistic as text in TextView with tag (Statistic.Name)
         *  @param layout Layout that contains statistic (TextView)
         *  @param tag Tag that was assigned to TextView by createStatsLineWithNameTag
         *
         */
        fun updateStatByTag(layout: ViewGroup,
                                    tag: Statistic.Name,
                                    statNameString: String,
                                    statValueString: String) {

            val statView = layout.findViewWithTag<LinearLayout>(tag)

            statView.findViewById<TextView>(R.id.statText).text = statNameString
            statView.findViewById<TextView>(R.id.statValue).text = statValueString
        }

        /**
         * Creates TextView, which contains statistic. TextView receives tag - name of statistic (Statistic.Name).
         * @param rootView ViewGroup that will contain statistic
         * @param statLayoutId Id of layout, which contains TextView that represents statistic
         * @param statName Name of statistic. It's a tag for a TextView
         */
        fun createTextViewStatWithTag(layoutInflater: LayoutInflater,
                                      rootView: ViewGroup,
                                      statLayoutId: Int,
                                      statName: Statistic.Name,
                                      statString: String): TextView {

            val statTextView = layoutInflater
                    .inflate(statLayoutId, rootView, false)

            statTextView.tag = (statName)
            (statTextView as TextView).text = statString

            return statTextView
        }

        fun createStatWithTag(layoutInflater: LayoutInflater,
                              rootView: ViewGroup,
                              statLayoutId: Int,
                              statName: Statistic.Name,
                              statNameString: String,
                              statValueString: String): LinearLayout {

            val statView = layoutInflater
                    .inflate(statLayoutId, rootView, false) as LinearLayout

            statView.tag = statName

            statView.findViewById<TextView>(R.id.statText).text = statNameString
            statView.findViewById<TextView>(R.id.statValue).text = statValueString

            return statView
        }

        fun getDefaultMarkerIconForColor(color: Int) =
                BitmapDescriptorFactory.defaultMarker(getColorHue(color))!!

        private fun getColorHue(color: Int): Float {
            val floatArray = FloatArray(3)
            ColorUtils.colorToHSL(color, floatArray)
            return floatArray[0]
        }
    }
}