package org.example.storage.presentation

import org.example.storage.application.DownloadService
import org.example.storage.application.dto.DownloadFile
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.content
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.header
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@ExtendWith(SpringExtension::class)
@WebMvcTest(DownloadController::class)
class DownloadControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var downloadService: DownloadService

    @MockBean
    private lateinit var httpHeaders: HttpHeaders

    @Test
    fun `download file should return file`() {
        val fileName = "testFile.txt"
        val fileContent = "File content".toByteArray()
        `when`(downloadService.downloadFile("path/to/file")).thenReturn(DownloadFile(fileName, fileContent))

        mockMvc.perform(get("/download/{path}", "path/to/file"))
            .andExpect(status().isOk)
            .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM))
            .andExpect(header().string(HttpHeaders.CONTENT_DISPOSITION, "form-data; name=\"attachment\"; filename=\"$fileName\""))
            .andExpect(content().bytes(fileContent))
    }
}
