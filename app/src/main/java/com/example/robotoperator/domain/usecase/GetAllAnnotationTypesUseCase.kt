package com.example.robotoperator.domain.usecase

import com.example.robotoperator.domain.repository.PointCloudRepository
import com.example.robotoperator.model.AnnotationType
import javax.inject.Inject

/**
 * Use case for getting all annotation types
 */
class GetAllAnnotationTypesUseCase @Inject constructor(
    private val repository: PointCloudRepository
) {
    /**
     * Get all annotation types that have point clouds stored
     * @return List of annotation types
     */
    suspend operator fun invoke(): List<AnnotationType> {
        return repository.getAllAnnotationTypes()
    }
} 