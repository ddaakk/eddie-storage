package org.example.storage.presentation

import org.example.storage.application.DownloadService
import org.springframework.cache.annotation.Cacheable
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class DownloadController(
    private val downloadService: DownloadService,
) {
    @GetMapping("/download/{path}")
    fun downloadFile(@PathVariable path: String): ResponseEntity<ByteArray> {
        val (fileName, fileArray) = downloadService.downloadFile(path)
        val httpHeaders = HttpHeaders()

        httpHeaders.contentType = MediaType.APPLICATION_OCTET_STREAM
        httpHeaders.contentLength = fileArray.size.toLong()
        httpHeaders.setContentDispositionFormData("attachment", fileName)

        return ResponseEntity(fileArray, httpHeaders, HttpStatus.OK)
    }
}