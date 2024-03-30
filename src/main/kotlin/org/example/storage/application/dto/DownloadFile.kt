package org.example.storage.application.dto

data class DownloadFile(
    val fileName: String,
    val fileArray: ByteArray,
)