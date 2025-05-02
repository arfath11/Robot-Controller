package com.example.robotoperator.domain.usecase

import com.example.robotoperator.domain.repository.PointCloudRepository
import com.example.robotoperator.model.AnnotationType
import javax.inject.Inject

/**
 * Use case for deleting a point cloud
 */
class DeletePointCloudUseCase @Inject constructor(
    private val repository: PointCloudRepository
) {
    /**
     * Delete all points for a specific annotation type
     * @param annotationType The type of annotation
     */
    suspend operator fun invoke(annotationType: AnnotationType) {
        repository.deletePointCloud(annotationType)
    }
} 