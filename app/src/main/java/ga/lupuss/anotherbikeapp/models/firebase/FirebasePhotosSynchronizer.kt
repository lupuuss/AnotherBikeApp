package ga.lupuss.anotherbikeapp.models.firebase

import android.net.Uri
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.storage.*
import com.google.gson.Gson
import ga.lupuss.anotherbikeapp.models.base.PathsGenerator
import ga.lupuss.anotherbikeapp.models.base.PhotosSynchronizer
import ga.lupuss.anotherbikeapp.models.dataclass.RoutePhoto
import timber.log.Timber
import java.lang.Exception
import java.util.*
import kotlin.concurrent.schedule

class FirebasePhotosSynchronizer(
        private val storage: FirebaseStorage,
        private val pathsGenerator: PathsGenerator,
        private val gson: Gson
        ) : PhotosSynchronizer {

    private val list: MutableList<RoutePhotoUploadTask> = mutableListOf()

    init {
        restore()
    }

    inner class RoutePhotoUploadTask(
            val routePhoto: RoutePhoto,
            var sessionUri: Uri? = null
    ): OnFailureListener,
            OnProgressListener<UploadTask.TaskSnapshot>,
            OnSuccessListener<UploadTask.TaskSnapshot> {

        private val reference = storage.getReference("images/${routePhoto.link}")
        private var uploadTask: StorageTask<UploadTask.TaskSnapshot>? = null
        private val isInProgress: Boolean
            get() {
                return uploadTask?.isInProgress ?: false
            }

        private var uriSaved = false

        fun upload() {

            uploadTask = if (sessionUri != null) {

                reference.putFile(
                        Uri.fromFile(pathsGenerator.getPathForPhoto(routePhoto.link)),
                        StorageMetadata.Builder().build(),
                        sessionUri
                )
            } else {

                reference.putFile(Uri.fromFile(pathsGenerator.getPathForPhoto(routePhoto.link)))
            }

            uploadTask!!
                    .addOnSuccessListener(this)
                    .addOnFailureListener(this)
                    .addOnProgressListener(this)
        }

        override fun onFailure(p0: Exception) {
            Timber.e(p0)
            val errorCode = (p0 as StorageException).errorCode

            if (errorCode != StorageException.ERROR_CANCELED) {

                Timer().schedule(10__000) {

                    sessionUri = null
                    cleanListeners()
                    upload()
                }
            }
        }

        override fun onProgress(p0: UploadTask.TaskSnapshot) {

            sessionUri = p0.uploadSessionUri

            if (sessionUri != null && !uriSaved) {
                uriSaved = true
                refreshBackup()
            }

            Timber.d("PROGRESS OF ${routePhoto.link} >> ${100.0 * p0.bytesTransferred / p0.totalByteCount}")
        }

        override fun onSuccess(p0: UploadTask.TaskSnapshot) {
            Timber.d("Photo uploaded: ${p0.downloadUrl}")
            cleanListeners()
            list.remove(this)
            removePhotoFile(routePhoto)
            refreshBackup()
        }

        fun cleanListeners() {
            uploadTask?.let {
                it.removeOnFailureListener(this)
                it.removeOnProgressListener(this)
                it.removeOnSuccessListener(this)
            }
        }

        fun cancel() {
            if (isInProgress) {
                uploadTask?.cancel()
            }
        }
    }


    private fun restore() {

        val file = pathsGenerator.photosSyncFile

        if (file.exists()) {

            Timber.d("Restoring photos synchronization process...")

            file.readLines().forEach {

                val data = gson.fromJson(it, RoutePhotoSerializableData::class.java)

                list.add(
                        RoutePhotoUploadTask(data.routePhoto, if (data.stringUri != null) Uri.parse(data.stringUri) else null)
                )
            }

            list.forEach {
                Timber.d("${it.routePhoto} ${it.sessionUri}")
                it.upload()
            }
        }

    }

    private fun refreshBackup() {

        val file = pathsGenerator.photosSyncFile

        file.outputStream().use { outputStream ->


            list.forEach {
                outputStream.write(
                        gson.toJson(RoutePhotoSerializableData(it.routePhoto, it.sessionUri))
                                .toByteArray()
                )
            }
        }

        Timber.d("Backup >>>")
        Timber.d(file.readLines().toString())
    }

    private fun remove(photo: RoutePhoto) {

        val task = list.find {
            it.routePhoto.link == photo.link
        }

        task?.let {
            it.cancel()
            it.cleanListeners()
            list.remove(it)
        }

        refreshBackup()

        val file = pathsGenerator.getPathForPhoto(photo.link)

        if (file.exists()) {

            file.delete()
        }
    }

    override fun removePhotoFile(photo: RoutePhoto) {

        val file = pathsGenerator.getPathForPhoto(photo.link)

        if (file.exists()) {

            file.delete()
        }
    }

    override fun removeAll(photos: List<RoutePhoto>) {

        photos.forEach {
            remove(it)
        }
    }

    override fun uploadAll(photos: List<RoutePhoto>) {

        photos.forEach {

            list.add(RoutePhotoUploadTask(it))
        }

        refreshBackup()

        list.forEach {
            it.upload()
        }
    }

    override fun cancelAll() {

        list.forEach {
            it.cancel()
        }

        list.forEach {
            it.cleanListeners()
        }
    }

    override fun getDownloadUrl(link: String, onSuccess: (String) -> Unit, onFail: () -> Unit) {
        storage.getReference("images/$link")
                .downloadUrl
                .addOnSuccessListener {
                    onSuccess(it.toString())
                }
                .addOnFailureListener {
                    Timber.e(it)
                    onFail()
                }
    }
}


private class RoutePhotoSerializableData (
        val routePhoto: RoutePhoto,
        sessionUri: Uri?
) {
    val stringUri: String? = sessionUri?.toString()
}