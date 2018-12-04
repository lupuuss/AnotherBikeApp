package ga.lupuss.anotherbikeapp.models.android

import android.content.Context
import android.os.Environment
import ga.lupuss.anotherbikeapp.R
import ga.lupuss.anotherbikeapp.models.base.AuthInteractor
import ga.lupuss.anotherbikeapp.models.base.PathsGenerator
import java.io.File

class AndroidPathsGenerator(private val context: Context,
                            private val timeProvider: () -> Long,
                            private val authInteractor: AuthInteractor) : PathsGenerator {

    override fun createNewPhotoFile(): File {

        val dir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        val time = timeProvider()
        val userName = authInteractor.userUid

        return File(dir, "$userName/$time.png")
    }
}