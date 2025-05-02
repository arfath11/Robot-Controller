package com.example.robotoperator.domain.usecase

import com.example.robotoperator.domain.repository.PointCloudRepository
import com.example.robotoperator.model.AnnotationType
import javax.inject.Inject
import kotlin.random.Random

/**
 * Use case for saving a point cloud
 */
class SavePointCloudUseCase @Inject constructor(
    private val repository: PointCloudRepository
) {
    /**
     * Save a point cloud for a specific annotation type
     * @param annotationType The type of annotation
     * @param points List of 3D points, each represented as a FloatArray of [x, y, z]
     */
    suspend operator fun invoke(annotationType: AnnotationType, points: List<FloatArray>) {
        repository.savePointCloud(annotationType, points)
    }
    
    /**
     * Generate and save random point data for testing
     * @param annotationType The annotation type for the points
     * @param count Number of random points to generate
     * @return The list of generated points
     */
    suspend fun saveRandomData(annotationType: AnnotationType, count: Int = 50): List<FloatArray> {
        val points = mutableListOf<FloatArray>()
        
        // Generate random points
        for (i in 0 until count) {
            val x = Random.nextFloat() * 20 - 10 // Range: -10 to 10
            val y = Random.nextFloat() * 20 - 10
            val z = Random.nextFloat() * 20 - 10
            points.add(floatArrayOf(x, y, z))
        }
        
        // Save the points
        repository.savePointCloud(annotationType, points)
        return points
    }
} 