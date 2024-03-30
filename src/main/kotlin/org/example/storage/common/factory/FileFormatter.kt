package org.example.storage.common.factory


object FileFormatter {
    fun buildFileName(originalFileName: String): String {
        val fileExtensionSeparator = "."
        val fileExtensionIndex: Int = originalFileName.lastIndexOf(fileExtensionSeparator)
        val fileExtension = originalFileName.substring(fileExtensionIndex)
        val fileName = originalFileName.substring(0, fileExtensionIndex)
        val now = System.currentTimeMillis().toString()

        return "${fileName}_${now}${fileExtension}"
    }
}