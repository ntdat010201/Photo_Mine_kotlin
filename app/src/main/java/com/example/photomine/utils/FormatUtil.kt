package com.example.photomine.utils

import android.media.ExifInterface
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit


class FormatUtil {
    companion object {

        fun formatFileSize(sizeInBytes: Long): String {
            val kb = sizeInBytes / 1024
            val mb = kb / 1024
            return when {
                mb > 0 -> "$mb MB"
                kb > 0 -> "$kb KB"
                else -> "$sizeInBytes bytes"
            }
        }

        fun formatFileDate(timestamp: Long): String {
            val date = Date(timestamp)
            val format = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
            return format.format(date)
        }

        fun getImageQualityBySize(sizeInBytes: Long): String {
            val sizeInMB = sizeInBytes / (1024 * 1024)
            return when {
                sizeInBytes > 5 -> "High"
                sizeInMB in 2..5 -> "Medium"
                else -> "Low"
            }
        }

        fun formatDuration(milliseconds: Int): String {
            val seconds = (milliseconds / 1000) % 60
            val minutes = (milliseconds / (1000 * 60)) % 60
            return String.format("%d:%02d", minutes, seconds)
        }

        fun getExifDate(filePath: String): Long {
            return try {
                val exif = androidx.exifinterface.media.ExifInterface(filePath)
                val dateTime = exif.getAttribute(ExifInterface.TAG_DATETIME_ORIGINAL)

                // Chuyển đổi thời gian từ định dạng "yyyy:MM:dd HH:mm:ss" sang milliseconds
                val formatter = java.text.SimpleDateFormat("yyyy:MM:dd HH:mm:ss", Locale.getDefault())
                val date = formatter.parse(dateTime ?: "")
                date?.time ?: System.currentTimeMillis() // Nếu không có thời gian, dùng thời gian hiện tại
            } catch (e: Exception) {
                e.printStackTrace()
                System.currentTimeMillis()
            }
        }

    }
}