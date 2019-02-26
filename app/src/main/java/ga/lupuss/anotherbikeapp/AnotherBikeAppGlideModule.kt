package ga.lupuss.anotherbikeapp

import android.content.Context
import com.firebase.ui.storage.images.FirebaseImageLoader
import com.google.firebase.storage.StorageReference
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule
import java.io.InputStream


@GlideModule
class AnotherBikeAppGlideModule : AppGlideModule() {

    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {

        registry.append(StorageReference::class.java, InputStream::class.java,
                FirebaseImageLoader.Factory())
    }

}