package org.example.storage.presentation

import jakarta.validation.Validator
import org.example.storage.application.UploadService
import org.example.storage.presentation.form.UploadForm
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.view

@WebMvcTest(UploadController::class)
class UploadControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var uploadService: UploadService

    @MockBean
    private lateinit var validator: Validator

    @Test
    fun `GET Index page should redirect to upload page`() {
        mockMvc.perform(get("/"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/upload"))
    }

    @Test
    fun `GET Upload page should return upload view`() {
        mockMvc.perform(get("/upload"))
            .andExpect(status().isOk)
            .andExpect(view().name("upload"))
    }

    @Test
    fun `POST Upload with valid form should redirect to uploaded page`() {
        val mockMultipartFile = MockMultipartFile(
            "files", "test.txt", MediaType.TEXT_PLAIN_VALUE, "test data".toByteArray()
        )

        val uploadForm = UploadForm()
        uploadForm.files = mutableListOf(mockMultipartFile)

        Mockito.`when`(uploadService.uploadFile(uploadForm)).thenReturn(listOf("path/to/file"))

        mockMvc.perform(multipart("/upload").file("files", mockMultipartFile.bytes))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/uploaded"))
    }

    @Test
    fun `GET Uploaded page should return uploaded view when there are uploaded files`() {
        mockMvc.perform(get("/uploaded").flashAttr("uploadedList", listOf("path/to/file")))
            .andExpect(status().isOk)
            .andExpect(view().name("uploaded"))
    }

    @Test
    fun `GET Uploaded page should redirect to upload page when there are no uploaded files`() {
        mockMvc.perform(get("/uploaded"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/upload"))
    }
}