package com.example.photomine.utils

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.photomine.model.ModelImage

@Dao
interface FavoriteDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavoriteFile(file: ModelImage)

    @Query("DELETE FROM favorite_item WHERE imageFile = :filePath")
    suspend fun removeFavoriteFile(filePath: String)

    @Query("SELECT * FROM favorite_item")
    fun getAllFavoriteFiles(): LiveData<List<ModelImage>>

    @Query("SELECT * FROM favorite_item WHERE imageFile = :filePath LIMIT 1")
    suspend fun isFileFavorite(filePath: String): ModelImage?

}