package ga.lupuss.anotherbikeapp.models
import com.google.gson.Gson
import java.io.File

class FilesManager(private val gson: Gson) {

    fun <T> readFileToObject(filePath: String, classObject: Class<T>): T {

        return gson.fromJson(File(filePath).readText(), classObject)
    }

    fun <T> saveObjectToFile(any: T, filePath: String): File {

        return File(filePath).apply {

            parentFile.mkdirs()
            if (!exists()) {
                createNewFile()
            }

            writeText(gson.toJson(any))
        }
    }

    fun makeChildrenListFor(path: String): List<File> {

        return File(path).walk().maxDepth(1)
                .iterator()
                .asSequence()
                .toMutableList()
                .apply { if (isNotEmpty()) removeAt(0) }
                .toList()
    }
}