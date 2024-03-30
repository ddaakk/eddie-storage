package org.example.storage.presentation

import jakarta.validation.Valid
import org.example.storage.application.UploadService
import org.example.storage.common.dto.ApiResponse
import org.example.storage.presentation.form.UploadForm
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.multipart.MultipartFile
import org.springframework.web.servlet.mvc.support.RedirectAttributes

@Controller
class UploadController(
    private val uploadService: UploadService,
) {
    @GetMapping("/")
    fun getIndexPage(): String {
        return "redirect:/upload"
    }

    @GetMapping("/upload")
    fun getUploadPage(model: Model): String {
        model.addAttribute("uploadForm", UploadForm())
        return "upload"
    }

    @PostMapping("/upload")
    fun uploadFile(
        @Valid uploadForm: UploadForm,
        result: BindingResult,
        redirectAttributes: RedirectAttributes
    ): String {
        if (result.hasErrors()) {
            return "upload"
        }

        val downloadPaths = uploadService.uploadFile(uploadForm)
        redirectAttributes.addFlashAttribute("uploadedList", downloadPaths)
        return "redirect:/uploaded"
    }

    @GetMapping("/uploaded")
    fun getUploadedPage(
        model: Model
    ): String {
        // 업로드 안됐을 시 업로드 페이지로 이동
        if (!model.containsAttribute("uploadedList")) {
            return "redirect:/upload"
        }

        model.addAttribute("uploadedList", model.getAttribute("uploadedList"))
        return "uploaded"
    }
}