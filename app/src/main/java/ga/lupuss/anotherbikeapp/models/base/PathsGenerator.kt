package ga.lupuss.anotherbikeapp.models.base

import java.io.File

interface PathsGenerator {

    fun createNewPhotoFile(link: String): File
}