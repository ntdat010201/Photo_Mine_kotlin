package com.example.photomine.base

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.photomine.model.ModelImage
import com.example.photomine.utils.FavoriteDao

@Database(entities = [ModelImage::class], version = 1)

abstract class FileDatabase : RoomDatabase() {

    abstract fun favoriteDao(): FavoriteDao

    companion object {
        @Volatile
        private var INSTANCE: FileDatabase? = null

        fun getDatabase(context: Context): FileDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FileDatabase::class.java,
                    "file_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}