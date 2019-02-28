package ga.lupuss.anotherbikeapp.models.dataclass

import com.google.firebase.storage.StorageReference
import java.io.File

sealed class ImageReference {
    class Firebase(
            val storageReference: StorageReference
    ): ImageReference()

    class Local(
            val file: File
    ): ImageReference()
}
