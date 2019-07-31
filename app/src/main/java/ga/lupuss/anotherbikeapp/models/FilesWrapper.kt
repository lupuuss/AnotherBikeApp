package ga.lupuss.anotherbikeapp.models

import java.io.File
import java.io.OutputStream
import java.security.MessageDigest

class FilesWrapper {

    fun readBytes(file: File): ByteArray = file.readBytes()

    fun readLines(file: File): List<String> = file.readLines()

    fun useOutputStream(file: File, use: (OutputStream) -> Unit) {

            file.outputStream().use(use)
        }

    fun sha256(file: File): String {
        val bytes = file.readBytes()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }
}