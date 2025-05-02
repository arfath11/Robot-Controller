package com.example.robotoperator.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

/**
 * Room database for the application
 * Using version 2 after updating primary key structure
 */
@Database(entities = [PointVertex::class], version = 2)
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
                    .fallbackToDestructiveMigration() // This will delete old data when schema changes
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
} 