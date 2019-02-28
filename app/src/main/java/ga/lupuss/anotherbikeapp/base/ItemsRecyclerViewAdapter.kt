package ga.lupuss.anotherbikeapp.base

import androidx.recyclerview.widget.RecyclerView

abstract class ItemsRecyclerViewAdapter<T : RecyclerView.ViewHolder> : RecyclerView.Adapter<T>() {

    protected val listeners = mutableListOf<OnItemClickListener>()

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        listeners.add(onItemClickListener)
    }
}