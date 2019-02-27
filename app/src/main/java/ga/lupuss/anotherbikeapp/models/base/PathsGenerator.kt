package ga.lupuss.anotherbikeapp.models.base

import java.io.File

interface PathsGenerator {

    fun getFileForPhotoLink(link: String): File
    fun getPhotoFileForTemp(file: File): File
    fun getTempPhotoFile(): File
    fun getLinkForFile(file: File): String
    fun getPhotosDir(): File
    fun getPhotosSyncFile(): File
}