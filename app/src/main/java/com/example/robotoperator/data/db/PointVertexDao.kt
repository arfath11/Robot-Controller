package com.example.robotoperator.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.robotoperator.model.AnnotationType

/**
 * Data Access Object for point vertices
 */
@Dao
interface PointVertexDao {
    /**
     * Insert or replace multiple points
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPoints(points: List<PointVertex>)
    
    /**
     * Get all points for a specific annotation type
     */
    @Query("SELECT * FROM point_vertices WHERE annotationType = :type")
    suspend fun getPointsByType(type: AnnotationType): List<PointVertex>
    
    /**
     * Get all distinct annotation types stored in the database
     */
    @Query("SELECT DISTINCT annotationType FROM point_vertices")
    suspend fun getAllAnnotationTypes(): List<AnnotationType>
    
    /**
     * Delete all points for a specific annotation type
     */
    @Query("DELETE FROM point_vertices WHERE annotationType = :type")
    suspend fun deletePointsByType(type: AnnotationType)
    
    /**
     * Delete all points in the database
     */
    @Query("DELETE FROM point_vertices")
    suspend fun deleteAllPoints()
} 