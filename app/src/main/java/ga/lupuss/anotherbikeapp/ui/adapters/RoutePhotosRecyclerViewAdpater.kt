package ga.lupuss.anotherbikeapp.ui.adapters

import android.support.constraint.ConstraintLayout
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import com.squareup.picasso.Picasso
import ga.lupuss.anotherbikeapp.R
import ga.lupuss.anotherbikeapp.base.ItemsRecyclerViewAdapter
import ga.lupuss.anotherbikeapp.models.dataclass.RoutePhoto
import ga.lupuss.anotherbikeapp.timeToFormattedString
import java.io.File
import java.util.*

class RoutePhotosRecyclerViewAdpater(
        private val picasso: Picasso,
        private val routePhotosCallback: (Int) -> RoutePhoto,
        private val sizeCallback: () -> Int,
        private val locale: Locale,
        private val onClickDeletePhoto: (Int) -> Unit
) : ItemsRecyclerViewAdapter<RoutePhotosRecyclerViewAdpater.ViewHolder>() {

    inner class ViewHolder(val layout: ConstraintLayout) : RecyclerView.ViewHolder(layout) {
        private val photoNameText: TextView = layout.findViewById(R.id.photoNameText)
        private val photoDateText: TextView = layout.findViewById(R.id.photoDateText)
        private val thumbnailView: ImageView = layout.findViewById(R.id.photoThumbnail)

        fun bindView(n: Int) {

            fillView(routePhotosCallback(n))
            layout.tag = this
        }

        private fun fillView(photo: RoutePhoto) {

            photoNameText.text = photo.name
            photoDateText.text = timeToFormattedString(locale, photo.time)
            picasso.load(File(photo.link))
                    .fit()
                    .centerCrop()
                    .into(thumbnailView)
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

        holder.itemView
                .findViewById<ImageButton>(R.id.deletePhotoButton)
                .setOnClickListener {

            onClickDeletePhoto(position)
        }

        holder.bindView(position)
    }
}