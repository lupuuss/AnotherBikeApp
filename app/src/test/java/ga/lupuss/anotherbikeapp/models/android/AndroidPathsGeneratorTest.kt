package ga.lupuss.anotherbikeapp.models.android

import android.content.Context
import android.os.Environment
import com.nhaarman.mockito_kotlin.any
import com.nhaarman.mockito_kotlin.mock
import ga.lupuss.anotherbikeapp.models.base.AuthInteractor
import ga.lupuss.anotherbikeapp.models.base.FilesWrapper
import org.junit.Test

import org.junit.Assert.*
import java.io.File

class AndroidPathsGeneratorTest {

    private val context: Context = mock {
        on { getExternalFilesDir(Environment.DIRECTORY_PICTURES) }
                .then { File("path/pictures") }
        on { filesDir }.then { File("path") }
    }

    private val picutresDir = File("path", "pictures")
    private val userDir = File(picutresDir, "1234")

    private val authInteractor: AuthInteractor = mock {
        on { userUid }.then { "1234" }
    }

    private val filesWrapper: FilesWrapper = mock { on { sha256(any()) }.then { "000" }}

    private val pathsGenerator = AndroidPathsGenerator(context, authInteractor, { 0 }, filesWrapper)

    @Test
    fun getFileForPhotoLink_shouldReturnProperPath() {

        assertEquals(
                File(userDir, "photo"),
                pathsGenerator.getFileForPhotoLink("1234/photo")
        )
    }

    @Test
    fun getPhotosSyncFile_shouldReturnProperPath() {

        assertEquals(File("path/photos.info"), pathsGenerator.getPhotosSyncFile())
    }

    @Test
    fun getLinkForFile_shouldReturnProperPath() {

        assertEquals("1234/000.png", pathsGenerator.getLinkForFile(mock {}))
    }

    @Test
    fun getTempPhotoFile_shouldReturnProperPath() {

        assertEquals(File("path/pictures/1234/0.png"), pathsGenerator.getTempPhotoFile())
    }

    @Test
    fun getPhotosDir_shouldReturnProperPath() {

        assertEquals(File("path/pictures/1234"), pathsGenerator.getPhotosDir())
    }

    @Test
    fun getPhotoFileForTemp_shouldReturnProperPath() {

        assertEquals(
                File("path/pictures/1234/000.png"),
                pathsGenerator.getPhotoFileForTemp(mock{})
        )
    }
}