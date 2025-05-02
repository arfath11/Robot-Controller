package com.example.robotoperator.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

/**
 * Room database for the application
 * Note: We don't need type converters as Room 2.3+ handles enums automatically
 * and our entity only contains primitive types and an enum.
 */
@Database(entities = [PointVertex::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun pointVertexDao(): PointVertexDao
    
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "robot_operator_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
} 