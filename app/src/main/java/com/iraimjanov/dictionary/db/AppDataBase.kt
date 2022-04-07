package com.iraimjanov.dictionary.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.iraimjanov.dictionary.models.Types
import com.iraimjanov.dictionary.models.Words

@Database(entities = [Types::class , Words::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun myDao(): MyDao

    companion object {
        private var instance: AppDatabase? = null

        @Synchronized
        fun getInstance(context: Context): AppDatabase {
            if (instance == null) {
                instance = Room.databaseBuilder(context, AppDatabase::class.java, "types")
                        .fallbackToDestructiveMigration().allowMainThreadQueries().build()
            }
            return instance!!
        }
    }
}