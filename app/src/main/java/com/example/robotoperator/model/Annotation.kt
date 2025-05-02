package com.example.robotoperator.model

import android.graphics.Color
import java.util.UUID

enum class AnnotationType(val displayName: String, val defaultColor: Int) {
    SPRAY_AREA("Spray Area", Color.RED),
    SAND_AREA("Sand Area", Color.YELLOW),
    OBSTACLE("Obstacle", Color.BLUE),
    CUSTOM("Custom", Color.GREEN);
}

/**
 * Data class representing a collection of points annotated in the model
 * @param id Unique identifier for this annotation
 * @param type The type of annotation (spray area, sand area, obstacle, etc.)
 * @param vertexIndices The indices of vertices in the model that are part of this annotation
 * @param color The color to use for this annotation (defaults to the type's default color)
 * @param description Optional description of this annotation
 * @param createdAt Timestamp when this annotation was created
 */
data class Annotation(
    val id: String = UUID.randomUUID().toString(),
    val type: AnnotationType,
    val vertexIndices: List<Float>,
    val color: Int = type.defaultColor,
)
