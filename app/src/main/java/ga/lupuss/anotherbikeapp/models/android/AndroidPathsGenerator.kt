package ga.lupuss.anotherbikeapp.models.android

import android.content.Context
import android.os.Environment
import ga.lupuss.anotherbikeapp.models.base.AuthInteractor
import ga.lupuss.anotherbikeapp.models.base.PathsGenerator
import ga.lupuss.anotherbikeapp.sha256ofFile
import java.io.File

class AndroidPathsGenerator(private val context: Context,
                            private val authInteractor: AuthInteractor,
                            private val timeProvider: () -> Long) : PathsGenerator {

    private val picturesDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)

    override fun getFileForPhotoLink(link: String): File =
            File(picturesDir, link)

    override fun getPhotosSyncFile(): File {

        val dir = context.filesDir

        return File(dir, "photos.info")
    }

    override fun getLinkForFile(file: File): String {

        val userUid = authInteractor.userUid
        val sha256 = sha256ofFile(file)

        return "$userUid/$sha256.png"
    }

    override fun getTempPhotoFile(): File {

        val userUid = authInteractor.userUid
        val time = timeProvider()
        val link = "$userUid/$time.png"

        return getFileForPhotoLink(link)
    }

    override fun getPhotosDir(): File = File(picturesDir, authInteractor.userUid)

    override fun getPhotoFileForTemp(file: File): File = getFileForPhotoLink(getLinkForFile(file))

}