package ga.lupuss.anotherbikeapp.ui.extensions

import android.annotation.SuppressLint
import android.support.v4.graphics.ColorUtils
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import ga.lupuss.anotherbikeapp.models.dataclass.Statistic

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
        fun updateTextViewStatByTag(layout: ViewGroup,
                                    tag: Statistic.Name, statString: String) {

            val statTextView = layout.findViewWithTag<TextView>(tag)

            statTextView.text = statString
        }

        /**
         * Create TextView, which contains statistic. TextView receives tag - name of statistic (Statistic.Name).
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

        fun getDefaultMarkerIconForColor(color: Int) =
                BitmapDescriptorFactory.defaultMarker(getColorHue(color))!!

        private fun getColorHue(color: Int): Float {
            val floatArray = FloatArray(3)
            ColorUtils.colorToHSL(color, floatArray)
            return floatArray[0]
        }
    }
}