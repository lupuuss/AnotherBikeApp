package ga.lupuss.anotherbikeapp.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import ga.lupuss.anotherbikeapp.R
import ga.lupuss.anotherbikeapp.ui.modules.main.MainActivity


class DrawerListViewAdapter(private val children: List<Pair<MainActivity.ItemName, MainActivity.StrIconRes>>,
                            private val layoutInflater: LayoutInflater) : BaseAdapter() {

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

            text = context.getString(children[position].second.str)
            setCompoundDrawablesWithIntrinsicBounds(children[position].second.icon,
                    0, 0, 0)
        }

        return textView
    }

    override fun getItem(position: Int): Any = children[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getCount(): Int = children.size
}