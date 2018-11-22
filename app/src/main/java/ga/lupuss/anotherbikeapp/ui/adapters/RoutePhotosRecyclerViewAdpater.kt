package ga.lupuss.anotherbikeapp.ui.adapters

import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import ga.lupuss.anotherbikeapp.R
import ga.lupuss.anotherbikeapp.base.ItemsRecyclerViewAdapter
import ga.lupuss.anotherbikeapp.models.dataclass.RoutePhoto

class RoutePhotosRecyclerViewAdpater(
        private val routePhotosCallback: (Int) -> RoutePhoto,
        private val sizeCallback: () -> Int
) : ItemsRecyclerViewAdapter<RoutePhotosRecyclerViewAdpater.ViewHolder>() {

    inner class ViewHolder(private val layout: ConstraintLayout) : RecyclerView.ViewHolder(layout) {
        private val photoNameText: TextView = layout.findViewById(R.id.photoNameText)
        private val photoDateText: TextView = layout.findViewById(R.id.photoDateText)
        private val thumbnailView: ImageView = layout.findViewById(R.id.photoThumbnail)

        fun bindView(n: Int) {

            fillView(routePhotosCallback(n))
            layout.tag = this
        }

        private fun fillView(photo: RoutePhoto) {

            photoNameText.text = photo.name
            photoDateText.text = photo.time.toString()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val constraintLayout = LayoutInflater
                .from(parent.context)
                .inflate(R.layout.fragment_route_photos_item, parent, false)
                as ConstraintLayout

        return ViewHolder(constraintLayout)
    }

    override fun getItemCount(): Int {

        return sizeCallback()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.itemView.setOnClickListener {

        }

        holder.bindView(position)
    }
}