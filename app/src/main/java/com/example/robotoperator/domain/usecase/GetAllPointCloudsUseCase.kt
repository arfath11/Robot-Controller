package com.example.robotoperator.domain.usecase

import com.example.robotoperator.domain.model.PointCloud
import com.example.robotoperator.domain.repository.PointCloudRepository
import javax.inject.Inject

/**
 * Use case for getting all point clouds from the database
 */
class GetAllPointCloudsUseCase @Inject constructor(
    private val repository: PointCloudRepository
) {
    /**
     * Get all points from the database as a list of PointCloud objects
     * @return List of PointCloud objects, each containing the annotation type and points
     */
    suspend operator fun invoke(): List<PointCloud> {
        // Get all points grouped by annotation type
        val groupedPoints = repository.getAllPointClouds()
        
        // Convert the map to a list of PointCloud objects
        return groupedPoints.map { (annotationType, points) ->
            PointCloud(annotationType, points)
        }
    }
} 