package com.example.robotoperator.domain.usecase

import com.example.robotoperator.domain.repository.PointCloudRepository
import javax.inject.Inject

/**
 * Use case for deleting all point clouds
 */
class DeleteAllPointCloudsUseCase @Inject constructor(
    private val repository: PointCloudRepository
) {
    /**
     * Delete all point clouds
     */
    suspend operator fun invoke() {
        repository.deleteAllPointClouds()
    }
} 