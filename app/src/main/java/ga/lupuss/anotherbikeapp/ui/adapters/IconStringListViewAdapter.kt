package ga.lupuss.anotherbikeapp.ui.adapters


import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import ga.lupuss.anotherbikeapp.R

class IconStringListViewAdapter(context: Context,
                                private val layoutInflater: LayoutInflater) : BaseAdapter() {

    private val children: List<Pair<String, Int>>

    init {

        val keys = context.resources.getStringArray(R.array.drawer_list_keys).toList()
        val values = context.resources.obtainTypedArray(R.array.drawer_list_values)

        val list = mutableListOf<Pair<String, Int>>()

        if (keys.size > values.length()) {

            throw IllegalStateException(
                    "Missing value for key ${keys.last()} in R.array.drawer_list_values"
            )
        }

        for (i in 0 until keys.size) {

            list.add(Pair(keys[i], values.getResourceId(i, 0)))
        }

        values.recycle()

        children = list.toList()

    }

    inner class ViewHolder(var textView: TextView)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val textView =

                if (convertView == null) {

                    val view = layoutInflater.inflate(
                            R.layout.activity_main_drawer_list_item,
                            parent,
                            false
                    )

                    view.findViewById<TextView>(R.id.itemText).apply {
                        tag = ViewHolder(this)
                    }

                } else {

                    convertView
                }

        (textView.tag as ViewHolder).textView.apply {

            text = children[position].first
            setCompoundDrawablesWithIntrinsicBounds(children[position].second,
                    0, 0, 0)
        }

        return textView
    }

    override fun getItem(position: Int): Any = children[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getCount(): Int = children.size
}