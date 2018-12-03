package ga.lupuss.anotherbikeapp.models.dataclass

import java.io.File

data class Photo(
   val link: String,
   val name: String,
   val time: Long
) {

    val file
        get() = File(link)
}