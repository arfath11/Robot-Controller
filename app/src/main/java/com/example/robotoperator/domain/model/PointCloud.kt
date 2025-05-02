package com.example.robotoperator.domain.model

import com.example.robotoperator.model.AnnotationType

/**
 * Domain model representing a cloud of 3D points with annotation information
 * @param annotationType The type of annotation this point cloud represents
 * @param points List of 3D points, each represented as a FloatArray of [x, y, z]
 */
data class PointCloud(
    val annotationType: AnnotationType,
    val points: List<FloatArray>
) 