package com.example.photomine.utils

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

    }
}