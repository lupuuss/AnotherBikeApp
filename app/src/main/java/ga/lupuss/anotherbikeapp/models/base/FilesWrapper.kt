package ga.lupuss.anotherbikeapp.models.base

import java.io.File
import java.io.OutputStream

interface FilesWrapper {
    fun readBytes(file: File): ByteArray
    fun readLines(file: File): List<String>
    fun useOutputStream(file: File, use: (OutputStream) -> Unit)
    fun sha256(file: File): String
}