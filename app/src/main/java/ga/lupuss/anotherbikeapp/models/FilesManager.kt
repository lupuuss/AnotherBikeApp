package ga.lupuss.anotherbikeapp.models
import com.google.gson.Gson
import java.io.File

@Suppress("MemberVisibilityCanBePrivate")
class FilesManager(private val gson: Gson) {

    fun <T> readFileToObject(filePath: String, classObject: Class<T>): T {

        return readFileToObject(File(filePath), classObject)
    }

    fun <T> readFileToObject(file: File, classObject: Class<T>): T {

        return gson.fromJson(file.readText(), classObject)
    }

    fun <T> saveObjectToFile(any: T, filePath: String): File {

        return saveObjectToFile(any, File(filePath))
    }

    fun <T> saveObjectToFile(any: T, file: File): File {

        return file.apply {

            parentFile.mkdirs()
            if (!exists()) {
                createNewFile()
            }

            writeText(gson.toJson(any))
        }

    }

    fun makeChildrenListFor(file: File): List<File> {

        return file.walk().maxDepth(1)
                .iterator()
                .asSequence()
                .toMutableList()
                .apply { if (isNotEmpty()) removeAt(0) }
                .toList()
    }
}