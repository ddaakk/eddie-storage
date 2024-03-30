package org.example.storage.application

import com.amazonaws.services.s3.AmazonS3
import com.amazonaws.services.s3.model.PutObjectRequest
import org.example.storage.domain.UploadedFile
import org.example.storage.infrastructure.UploadedFileRepository
import org.example.storage.presentation.form.UploadForm
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentCaptor
import org.mockito.Captor
import org.mockito.Mock
import org.mockito.Mockito.any
import org.mockito.Mockito.doThrow
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.jupiter.MockitoExtension
import org.springframework.mock.web.MockMultipartFile

@ExtendWith(MockitoExtension::class)
class UploadServiceTest {

    @Mock
    private lateinit var amazonS3: AmazonS3

    @Mock
    private lateinit var uploadedFileRepository: UploadedFileRepository

    private lateinit var uploadService: UploadService

    @Captor
    private lateinit var putObjectRequestCaptor: ArgumentCaptor<PutObjectRequest>

    @Captor
    private lateinit var uploadedFileCaptor: ArgumentCaptor<UploadedFile>

    @BeforeEach
    fun setUp() {
        uploadService = UploadService(amazonS3, uploadedFileRepository, "test-bucket")
    }

    @Test
    fun `upload file should save uploaded file and return download path`() {
        val files = listOf(
            MockMultipartFile("file", "test.txt", "text/plain", "Test content".toByteArray())
        )
        val uploadForm = UploadForm(files, 60, 5)

        `when`(uploadedFileRepository.save(any(UploadedFile::class.java))).thenAnswer { it.arguments[0] }

        val downloadPaths = uploadService.uploadFile(uploadForm)

        assertEquals(1, downloadPaths.size)
        verify(amazonS3, times(1)).putObject(putObjectRequestCaptor.capture())
        verify(uploadedFileRepository, times(1)).save(uploadedFileCaptor.capture())

        val putObjectRequest = putObjectRequestCaptor.value
        assertEquals("test-bucket", putObjectRequest.bucketName)
        assertEquals(files[0].contentType, putObjectRequest.metadata.contentType)
        assertEquals(files[0].size, putObjectRequest.metadata.contentLength)

        val uploadedFile = uploadedFileCaptor.value
        assertEquals(5, uploadedFile.maxDownloads)
    }

    @Test
    fun `upload file with empty files should throw IllegalArgumentException`() {
        val uploadForm = UploadForm(emptyList(), 60, 5)

        assertThrows<IllegalArgumentException> {
            uploadService.uploadFile(uploadForm)
        }
    }

    @Test
    fun `upload file should throw IllegalArgumentException on S3 failure`() {
        val files = listOf(
            MockMultipartFile("file", "test.txt", "text/plain", "Test content".toByteArray())
        )
        val uploadForm = UploadForm(files, 60, 5)

        doThrow(IllegalArgumentException::class.java).`when`(amazonS3).putObject(any(PutObjectRequest::class.java))

        assertThrows<IllegalArgumentException> {
            uploadService.uploadFile(uploadForm)
        }
    }

}
