package ga.lupuss.anotherbikeapp.models.android

import android.content.Context
import android.os.Environment
import ga.lupuss.anotherbikeapp.models.base.PathsGenerator
import java.io.File

class AndroidPathsGenerator(private val context: Context) : PathsGenerator {

    override fun getPathForPhotoLink(link: String): File {

        val dir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        return File(dir, link)
    }

    override val photosSyncFile: File
        get() {

        val dir = context.filesDir

        return File(dir, "photos.info")
    }
}