package com.example.photomine.usecase.media

import android.app.Application
import android.provider.MediaStore
import com.example.photomine.model.ModelImage
import com.example.photomine.utils.FormatUtil.Companion.getExifDate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LoadMediaUseCase(
    private val application: Application
) {

    suspend fun loadImagesAndVideos(): Pair<List<ModelImage>, List<ModelImage>> {
        return withContext(Dispatchers.IO) {
            val images = mutableListOf<ModelImage>()
            val videos = mutableListOf<ModelImage>()

            // Truy vấn ảnh
            val projection = arrayOf(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DATE_ADDED,
                MediaStore.Images.Media.SIZE,
                MediaStore.Images.Media.DISPLAY_NAME
            )

            val cursor = application.contentResolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                "${MediaStore.Images.Media.DATE_ADDED} DESC"
            )

            cursor?.use {
                val pathColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
                val sizeColumn = it.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)

                while (it.moveToNext()) {
                    val imagePath = it.getString(pathColumn)
                    val size = it.getLong(sizeColumn)
                    val dateTaken = getExifDate(imagePath)

                    images.add(ModelImage(0, imagePath, dateTaken, size, "High"))
                }
            }

            // Truy vấn video
            val videoProjection = arrayOf(
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.SIZE,
                MediaStore.Video.Media.DISPLAY_NAME
            )

            val videoCursor = application.contentResolver.query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                videoProjection,
                null,
                null,
                "${MediaStore.Video.Media.DATE_ADDED} DESC"
            )

            videoCursor?.use {
                val pathColumn = it.getColumnIndexOrThrow(MediaStore.Video.Media.DATA)
                val sizeColumn = it.getColumnIndexOrThrow(MediaStore.Video.Media.SIZE)

                while (it.moveToNext()) {
                    val imagePath = it.getString(pathColumn)
                    val size = it.getLong(sizeColumn)
                    val dateTaken = getExifDate(imagePath)

                    videos.add(ModelImage(0, imagePath, dateTaken, size, "High"))
                }
            }

            Pair(images, videos)
        }
    }

}