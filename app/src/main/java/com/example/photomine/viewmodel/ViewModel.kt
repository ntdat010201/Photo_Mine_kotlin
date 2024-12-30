package com.example.photomine.viewmodel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.photomine.base.FileDatabase
import com.example.photomine.model.ModelImage
import com.example.photomine.usecase.delete.FileDeleteUseCase
import com.example.photomine.usecase.favorite.FavoriteFilesUseCase
import com.example.photomine.usecase.media.LoadMediaUseCase
import kotlinx.coroutines.launch

class ViewModel(application: Application) : AndroidViewModel(application) {


    private val loadMediaUseCase = LoadMediaUseCase(application)
    private val favoriteFilesUseCase =
        FavoriteFilesUseCase(FileDatabase.getDatabase(application).favoriteDao())
    private val fileDeleteUseCase = FileDeleteUseCase(application)

    private val imagesMutableLiveData = MutableLiveData<List<ModelImage>>()
    private val videosMutableLiveData = MutableLiveData<List<ModelImage>>()
    private val deleteResultMutableLiveData = MutableLiveData<Result<ModelImage>>()


    val imagesLiveData: LiveData<List<ModelImage>> get() = imagesMutableLiveData
    val videosLiveData: LiveData<List<ModelImage>> get() = videosMutableLiveData
    val deleteResultLiveData: LiveData<Result<ModelImage>> get() = deleteResultMutableLiveData
    val favoriteLiveData: LiveData<List<ModelImage>> get() = favoriteFilesUseCase.getFavoriteFiles()


    // thêm hình ảnh mới vào danh sách (chụp ảnh)
    fun addImage(imageItem: ModelImage) {
        val currentList = imagesMutableLiveData.value?.toMutableList() ?: mutableListOf()
        currentList.add(imageItem)
        imagesMutableLiveData.value = currentList
    }

    fun addVideo(modelVideo: ModelImage) {
        val updatedList = videosMutableLiveData.value?.toMutableList() ?: mutableListOf()
        updatedList.add(modelVideo)
        videosMutableLiveData.value = updatedList
    }

    fun loadImagesAndVideos() {
        viewModelScope.launch {
            val (imageList, videoList) = loadMediaUseCase.loadImagesAndVideos()
            imagesMutableLiveData.value = imageList
            videosMutableLiveData.value = videoList
        }
    }

    fun addFavoriteFile(file: ModelImage) {
        viewModelScope.launch {
            favoriteFilesUseCase.addFavoriteFile(file)
        }
    }

    fun removeFavoriteFile(file: ModelImage) {
        viewModelScope.launch {
            favoriteFilesUseCase.removeFavoriteFile(file)
        }
    }

    suspend fun isFileFavorite(fileUri: String): Boolean {
        return favoriteFilesUseCase.isFileFavorite(fileUri)
    }



    // Xóa file hình ảnh
    fun deleteImage(file: ModelImage) {
        viewModelScope.launch {
            try {
                val result = fileDeleteUseCase.deleteFile(file.imageFile, Uri.parse(file.imageFile))
                if (result) {
                    deleteResultMutableLiveData.value = Result.success(file) // Thành công
                } else {
                    deleteResultMutableLiveData.value = Result.failure(Exception("Failed to delete file")) // Thất bại
                }
            } catch (e: Exception) {
                deleteResultMutableLiveData.value = Result.failure(e) // Xử lý lỗi
            }
        }
    }


}