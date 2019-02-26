package ga.lupuss.anotherbikeapp.models.base

import java.io.File

interface PathsGenerator {

    val photosSyncFile: File
    fun getPathForPhotoLink(link: String): File
}