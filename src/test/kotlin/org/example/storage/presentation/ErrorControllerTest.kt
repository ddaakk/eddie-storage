package org.example.storage.presentation

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*

@WebMvcTest(ErrorController::class)
class ErrorControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    fun `GET Error page should redirect to upload page when there is no error message`() {
        mockMvc.perform(get("/error"))
            .andExpect(status().is3xxRedirection())
            .andExpect(redirectedUrl("/upload"))
    }

    @Test
    fun `GET Error page should return error view when there is an error message`() {
        mockMvc.perform(get("/error").flashAttr("errorMessage", "Error occurred"))
            .andExpect(status().isOk)
            .andExpect(view().name("error"))
            .andExpect(model().attribute("errorMessage", "Error occurred"))
    }
}