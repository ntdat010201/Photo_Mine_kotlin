package com.example.photomine.usecase.delete

import android.app.Application
import android.content.ContentResolver
import android.net.Uri
import android.util.Log
import java.io.File

class FileDeleteUseCase(private val application: Application) {

    // Hàm này sẽ xóa file từ đường dẫn hoặc MediaStore
    fun deleteFile(filePath: String, imageUri: Uri?): Boolean {
        // Nếu filePath hợp lệ
        if (filePath.isNotEmpty()) {
            val file = File(filePath)
            return if (file.exists()) {
                val deleted = file.delete()
                Log.d("DeleteFile", if (deleted) "File deleted from local storage" else "Failed to delete file")
                deleted
            } else {
                Log.d("DeleteFile", "File does not exist at the given path")
                false
            }
        }

        // Nếu imageUri hợp lệ (xóa từ MediaStore)
        imageUri?.let {
            val contentResolver: ContentResolver = application.contentResolver
            try {
                val rowsDeleted = contentResolver.delete(it, null, null)
                Log.d("DeleteFile", if (rowsDeleted > 0) "File deleted from MediaStore" else "Failed to delete file from MediaStore")
                return rowsDeleted > 0
            } catch (e: Exception) {
                Log.e("DeleteFile", "Error while deleting file: ${e.message}")
            }
        }

        return false
    }
}