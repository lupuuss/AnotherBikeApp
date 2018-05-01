package ga.lupuss.anotherbikeapp.models

import android.content.Context
import java.io.File

class PathsGenerator(private val context: Context) {

    fun generateUnsyncRoutesPathForUserName(userName: String) =
            File(context.filesDir, "/users/$userName/routes/unsync")

    fun generateSyncRoutesPathForUserName(userName: String) =
            File(context.filesDir, "/users/$userName/routes/sync")
}