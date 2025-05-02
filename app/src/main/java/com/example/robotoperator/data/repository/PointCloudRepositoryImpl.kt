package com.example.robotoperator.data.repository

import com.example.robotoperator.data.db.PointVertex
import com.example.robotoperator.data.db.PointVertexDao
import com.example.robotoperator.domain.repository.PointCloudRepository
import com.example.robotoperator.model.AnnotationType
import javax.inject.Inject

/**
 * Implementation of PointCloudRepository
 */
class PointCloudRepositoryImpl @Inject constructor(
    private val pointVertexDao: PointVertexDao
) : PointCloudRepository {
    
    override suspend fun savePointCloud(annotationType: AnnotationType, points: List<FloatArray>) {
        val vertices = points.map { point ->
            PointVertex(
                x = point[0],
                y = point[1],
                z = point[2],
                annotationType = annotationType
            )
        }
        pointVertexDao.insertPoints(vertices)
    }
    
    override suspend fun getPointCloud(annotationType: AnnotationType): List<FloatArray> {
        return pointVertexDao.getPointsByType(annotationType).map { vertex ->
            floatArrayOf(vertex.x, vertex.y, vertex.z)
        }
    }
    
    override suspend fun getAllAnnotationTypes(): List<AnnotationType> {
        return pointVertexDao.getAllAnnotationTypes()
    }
    
    override suspend fun deletePointCloud(annotationType: AnnotationType) {
        pointVertexDao.deletePointsByType(annotationType)
    }
    
    override suspend fun deleteAllPointClouds() {
        pointVertexDao.deleteAllPoints()
    }
} 