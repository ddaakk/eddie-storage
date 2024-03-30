package org.example.storage.presentation

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
class ErrorController {
    @GetMapping("/error")
    fun getErrorPage(model: Model): String {
        if (!model.containsAttribute("errorMessage")) {
            return "redirect:/upload"
        }

        model.addAttribute("errorMessage", model.getAttribute("errorMessage"))
        return "error"
    }
}