package ga.lupuss.anotherbikeapp.ui.extensions

import android.content.res.Resources
import android.util.TypedValue

fun Resources.Theme.getColorForAttr(attr: Int): Int {
    val typedValue = TypedValue()
    resolveAttribute(attr, typedValue, true)
    return typedValue.data
}