package com.example.robotoperator.domain.usecase

import com.example.robotoperator.domain.repository.PointCloudRepository
import com.example.robotoperator.model.AnnotationType
import javax.inject.Inject

/**
 * Use case for getting a point cloud
 */
class GetPointCloudUseCase @Inject constructor(
    private val repository: PointCloudRepository
) {
    /**
     * Get all points for a specific annotation type
     * @param annotationType The type of annotation
     * @return List of 3D points, each represented as a FloatArray of [x, y, z]
     */
    suspend operator fun invoke(annotationType: AnnotationType): List<FloatArray> {
        return repository.getPointCloud(annotationType)
    }
} 