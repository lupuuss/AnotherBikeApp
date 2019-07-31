package ga.lupuss.anotherbikeapp.models.firebase

import android.net.Uri
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.storage.*
import com.google.gson.Gson
import ga.lupuss.anotherbikeapp.models.FilesWrapper
import ga.lupuss.anotherbikeapp.models.base.PathsGenerator
import ga.lupuss.anotherbikeapp.models.base.PhotosSynchronizer
import ga.lupuss.anotherbikeapp.models.dataclass.MarkedRoutePhoto
import ga.lupuss.anotherbikeapp.models.dataclass.RoutePhoto
import timber.log.Timber
import java.io.File
import java.lang.Exception
import java.util.*
import kotlin.concurrent.schedule

class FirebasePhotosSynchronizer(
        private val storage: FirebaseStorage,
        private val pathsGenerator: PathsGenerator,
        private val gson: Gson,
        private val filesWrapper: FilesWrapper
        ) : PhotosSynchronizer {

    private val uploadTasks: MutableList<RoutePhotoUploadTask> = mutableListOf()

    init {
        restore()
    }

    inner class RoutePhotoUploadTask(
            val routePhoto: MarkedRoutePhoto,
            var sessionUri: Uri? = null,
            val photoFile: File = pathsGenerator.getFileForPhotoLink(routePhoto.link)
    ): OnFailureListener,
            OnProgressListener<UploadTask.TaskSnapshot>,
            OnSuccessListener<UploadTask.TaskSnapshot> {

        private val reference = getStorageReference(routePhoto.link)
        private var uploadTask: StorageTask<UploadTask.TaskSnapshot>? = null
        private val isInProgress: Boolean
            get() {
                return uploadTask?.isInProgress ?: false
            }

        private var uriSaved = false

        fun upload() {

            uploadTask = if (sessionUri != null) {

                reference.putFile(
                        Uri.fromFile(photoFile),
                        StorageMetadata.Builder().build(),
                        sessionUri
                )
            } else {

                reference.putFile(Uri.fromFile(photoFile))
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
                    uriSaved = false
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
            Timber.d("Photo uploaded: ${p0.uploadSessionUri.toString()}")
            cleanListeners()
            uploadTasks.remove(this)
            removeFile(routePhoto)
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

        val file = pathsGenerator.getPhotosSyncFile()

        if (file.exists()) {

            filesWrapper.forEachFileLine(file) {

                try {

                    val data = gson.fromJson(it, RoutePhotoSerializableData::class.java)

                    uploadTasks.add(
                            RoutePhotoUploadTask(data.routePhoto, if (data.stringUri != null) Uri.parse(data.stringUri) else null)
                    )

                } catch (e: Exception) {

                    e.printStackTrace()
                }
            }

            Timber.d("Restored uploads: ")

            uploadTasks.forEach {

                Timber.d("${it.routePhoto} ${it.sessionUri}")
                it.upload()
            }
        }

        deleteUnusedFiles()
    }

    private fun deleteUnusedFiles() {

        val dir = pathsGenerator.getPhotosDir()

        if (dir.isDirectory && dir.exists()) {

            filesWrapper.forEachFileInDir(dir) { file ->

                if (file.isFile && uploadTasks.find { it.photoFile == file } == null) {

                    file.delete()
                }
            }
        }
    }

    private fun refreshBackup() {

        val file = pathsGenerator.getPhotosSyncFile()

        filesWrapper.useBufferedWriter(file) { bufferedWriter ->


            uploadTasks.forEach {

                bufferedWriter.append(gson.toJson(RoutePhotoSerializableData(it.routePhoto, it.sessionUri)))
                bufferedWriter.appendln()
            }
        }

        Timber.d("Backup >>>")
        Timber.d(filesWrapper.readLines(file).toString())
    }

    override fun rejectUpload(photo: MarkedRoutePhoto) {

        val task = uploadTasks.find {
            it.routePhoto.link == photo.link
        }

        task?.let {
            it.cancel()
            it.cleanListeners()
            uploadTasks.remove(it)
        }

        refreshBackup()

        val file = pathsGenerator.getFileForPhotoLink(photo.link)

        if (file.exists()) {

            file.delete()
        }
    }

    override fun rejectAllUploadsForRoute(routeId: String) {

        val tasksList = mutableListOf<RoutePhotoUploadTask>()

        uploadTasks.forEach {

            if (it.routePhoto.routeId == routeId) {

                tasksList.add(it)
            }
        }

        tasksList.forEach {
            it.cancel()
            it.cleanListeners()
            uploadTasks.remove(it)
        }

        refreshBackup()
    }

    override fun removeFile(photo: RoutePhoto) {

        val file = pathsGenerator.getFileForPhotoLink(photo.link)

        if (file.exists()) {

            file.delete()
        }
    }

    override fun uploadAll(photos: List<MarkedRoutePhoto>) {

        photos.forEach {

            uploadTasks.add(RoutePhotoUploadTask(it))
        }

        refreshBackup()

        uploadTasks.forEach {
            it.upload()
        }
    }

    override fun cancelAllUploads() {

        uploadTasks.forEach {
            it.cancel()
        }

        uploadTasks.forEach {
            it.cleanListeners()
        }
    }

    fun getStorageReference(link: String): StorageReference {

        return storage.getReference("images/$link")
    }

    override fun getPathForPhotoLink(link: String): File {

        return pathsGenerator.getFileForPhotoLink(link)
    }
}


private class RoutePhotoSerializableData (
        val routePhoto: MarkedRoutePhoto,
        sessionUri: Uri?
) {
    val stringUri: String? = sessionUri?.toString()
}