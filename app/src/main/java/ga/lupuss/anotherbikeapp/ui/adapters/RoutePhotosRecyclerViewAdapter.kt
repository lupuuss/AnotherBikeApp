package ga.lupuss.anotherbikeapp.ui.adapters

import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import ga.lupuss.anotherbikeapp.GlideApp
import ga.lupuss.anotherbikeapp.R
import ga.lupuss.anotherbikeapp.base.ItemsRecyclerViewAdapter
import ga.lupuss.anotherbikeapp.models.dataclass.RoutePhoto
import ga.lupuss.anotherbikeapp.models.firebase.FirebaseRoutesManager
import ga.lupuss.anotherbikeapp.timeToFormattedString
import kotlinx.android.synthetic.main.fragment_route_photos_item.view.*
import java.util.*

class RoutePhotosRecyclerViewAdapter(
        private val routePhotosCallback: (Int) -> RoutePhoto,
        private val sizeCallback: () -> Int,
        private val locale: Locale,
        private val routesManager: FirebaseRoutesManager,
        private val forceLocalPictures: Boolean
) : ItemsRecyclerViewAdapter<RoutePhotosRecyclerViewAdapter.ViewHolder>() {

    private val deletePhotoListeners: MutableList<(Int, View) -> Unit> = mutableListOf()
    private val photoThumbnailListeners: MutableList<(Int, View) -> Unit> = mutableListOf()

    fun addOnClickDeletePhotoListener(onClickDeletePhotoListener: (Int, View) -> Unit) {

        deletePhotoListeners.add(onClickDeletePhotoListener)
    }

    fun removeOnClickDeletePhotoListener(onClickDeletePhotoListener: (Int, View) -> Unit) {


        deletePhotoListeners.remove(onClickDeletePhotoListener)
    }

    fun addOnClickPhotoThumbnailListener(onClickPhotoListener: (Int, View) -> Unit) {

        photoThumbnailListeners.add(onClickPhotoListener)
    }

    fun removeOnClickPhotoThumbnailListener(onClickPhotoListener: (Int, View) -> Unit) {

        photoThumbnailListeners.remove(onClickPhotoListener)
    }

    inner class ViewHolder(val layout: ConstraintLayout) : RecyclerView.ViewHolder(layout) {
        private val photoNameText: TextView = layout.findViewById(R.id.photoNameText)
        private val photoDateText: TextView = layout.findViewById(R.id.photoDateText)
        private val thumbnailView: ImageView = layout.findViewById(R.id.photoThumbnail)

        fun bindView(n: Int) {

            fillView(routePhotosCallback(n))
            layout.tag = this
        }

        private fun fillView(photo: RoutePhoto) {

            photoNameText.text = photo.name ?: layout.context.getString(R.string.no_title)
            photoDateText.text = timeToFormattedString(locale, photo.time)

            val file = routesManager.getPathForRoutePhoto(photo)

            val glideRequest = if (forceLocalPictures || file.exists()) {

                GlideApp.with(this.layout.context)
                        .load(file)

            } else {

                GlideApp.with(this.layout.context)
                        .load(routesManager.getStoragePhotoReference(photo.link))
            }

            glideRequest
                    .fitCenter()
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

        holder.itemView.photoThumbnail.setOnClickListener { view ->

            photoThumbnailListeners.forEach {
                it(position, view)
            }
        }

        holder.itemView
                .findViewById<ImageButton>(R.id.deletePhotoButton)
                .setOnClickListener { view ->

            deletePhotoListeners.forEach {
                it(position, view)
            }
        }

        holder.bindView(position)
    }
}