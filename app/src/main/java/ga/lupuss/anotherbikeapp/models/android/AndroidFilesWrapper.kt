package ga.lupuss.anotherbikeapp.models.android

import ga.lupuss.anotherbikeapp.models.base.FilesWrapper
import java.io.File
import java.io.OutputStream
import java.security.MessageDigest

class AndroidFilesWrapper : FilesWrapper {
    override fun readBytes(file: File): ByteArray = file.readBytes()

    override fun readLines(file: File): List<String> = file.readLines()

    override fun useOutputStream(file: File, use: (OutputStream) -> Unit) {

        file.outputStream().use(use)
    }

    override fun sha256(file: File): String {
        val bytes = file.readBytes()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }
}