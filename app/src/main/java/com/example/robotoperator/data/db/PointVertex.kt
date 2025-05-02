package com.example.robotoperator.data.db

import androidx.room.Entity
import com.example.robotoperator.model.AnnotationType

/**
 * Entity representing a 3D point (vertex) with annotation information
 * Each point is uniquely identified by its x, y, z coordinates
 * When a point with the same coordinates is saved again, its annotation type will be updated
 */
@Entity(tableName = "point_vertices", primaryKeys = ["x", "y", "z"])
data class PointVertex(
    val x: Float,
    val y: Float,
    val z: Float,
    val annotationType: AnnotationType
) 