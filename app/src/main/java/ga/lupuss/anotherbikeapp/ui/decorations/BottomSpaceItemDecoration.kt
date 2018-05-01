package ga.lupuss.anotherbikeapp.ui.decorations

import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.view.View

class BottomSpaceItemDecoration(private val mSpaceHeight: Int) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        outRect.bottom = mSpaceHeight
    }
}