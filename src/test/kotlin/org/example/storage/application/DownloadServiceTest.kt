package org.example.storage.application

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.S3Object
import com.amazonaws.services.s3.model.S3ObjectInputStream
import org.example.storage.application.dto.DownloadFile
import org.example.storage.domain.UploadedFile
import org.example.storage.infrastructure.UploadedFileRepository
import org.junit.jupiter.api.Assertions.assertArrayEquals
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import java.io.ByteArrayInputStream
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
@SpringBootTest
class DownloadServiceTest {

    @MockBean
    private lateinit var amazonS3: AmazonS3

    @MockBean
    private lateinit var uploadedFileRepository: UploadedFileRepository

    @Autowired
    private lateinit var downloadService: DownloadService

    private lateinit var uploadedFile: UploadedFile
    private val fileName = "testFile.txt"
    private val fileContent = "File content".toByteArray()

    @BeforeEach
    fun setUp() {
        val s3Object = S3Object().apply {
            objectContent = S3ObjectInputStream(ByteArrayInputStream(fileContent), null)
        }

        `when`(amazonS3.getObject(anyString(), anyString())).thenReturn(s3Object)
        `when`(amazonS3.doesObjectExist(anyString(), anyString())).thenReturn(true)

        uploadedFile = UploadedFile(
            fileName = fileName,
            downloadPath = "path/to/file",
            maxDownloads = 5,
            downloadAbleTime = LocalDateTime.now().plusDays(1)
        )
    }

    @Test
    fun `download file should return valid file`() {
        `when`(uploadedFileRepository.findByDownloadPath("path/to/file")).thenReturn(uploadedFile)

        val result = downloadService.downloadFile("path/to/file")

        assertArrayEquals(fileContent, result.fileArray, "The returned file content should match the expected content")
        assertEquals(fileName, result.fileName, "The returned file name should match the expected name")
    }

    @Test
    fun `download file with expired download time should throw exception`() {
        uploadedFile.downloadAbleTime = LocalDateTime.now().minusDays(1)
        `when`(uploadedFileRepository.findByDownloadPath("path/to/file")).thenReturn(uploadedFile)

        assertThrows(IllegalArgumentException::class.java) {
            downloadService.downloadFile("path/to/file")
        }
    }

    @Test
    fun `download file with exceeded download limit should throw exception`() {
        uploadedFile.maxDownloads = 0
        `when`(uploadedFileRepository.findByDownloadPath("path/to/file")).thenReturn(uploadedFile)

        assertThrows(IllegalArgumentException::class.java) {
            downloadService.downloadFile("path/to/file")
        }
    }

    @Test
    fun `download non-existent file should throw exception`() {
        `when`(uploadedFileRepository.findByDownloadPath("non-existent/path")).thenReturn(null)

        assertThrows(IllegalArgumentException::class.java) {
            downloadService.downloadFile("non-existent/path")
        }
    }
}
