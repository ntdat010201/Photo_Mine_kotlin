package com.example.photomine.usecase.favorite

import androidx.lifecycle.LiveData
import com.example.photomine.model.ModelImage
import com.example.photomine.utils.FavoriteDao

class  FavoriteFilesUseCase(private val fileDao: FavoriteDao) {

    suspend fun addFavoriteFile(file: ModelImage) {
        fileDao.addFavoriteFile(file)
    }

    suspend fun removeFavoriteFile(file: ModelImage) {
        fileDao.removeFavoriteFile(file.imageFile)
    }

    suspend fun isFileFavorite(fileUri: String): Boolean {
        return fileDao.isFileFavorite(fileUri) != null
    }

     fun getFavoriteFiles(): LiveData<List<ModelImage>> {
        return fileDao.getAllFavoriteFiles()
    }
}