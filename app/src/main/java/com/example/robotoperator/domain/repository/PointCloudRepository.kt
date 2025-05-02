package com.example.robotoperator.domain.repository

import com.example.robotoperator.model.AnnotationType

/**
 * Repository interface for point cloud operations
 */
interface PointCloudRepository {
    /**
     * Save a point cloud for a specific annotation type
     * @param annotationType The type of annotation
     * @param points List of 3D points, each represented as a FloatArray of [x, y, z]
     */
    suspend fun savePointCloud(annotationType: AnnotationType, points: List<FloatArray>)
    
    /**
     * Get all points for a specific annotation type
     * @param annotationType The type of annotation
     * @return List of 3D points, each represented as a FloatArray of [x, y, z]
     */
    suspend fun getPointCloud(annotationType: AnnotationType): List<FloatArray>
    
    /**
     * Get all annotation types that have point clouds stored
     * @return List of annotation types
     */
    suspend fun getAllAnnotationTypes(): List<AnnotationType>
    
    /**
     * Delete all points for a specific annotation type
     * @param annotationType The type of annotation
     */
    suspend fun deletePointCloud(annotationType: AnnotationType)
    
    /**
     * Delete all point clouds
     */
    suspend fun deleteAllPointClouds()
} 