package com.example.robotoperator.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.robotoperator.model.AnnotationType
import com.example.robotoperator.opengl.renderer.CustomModelSurfaceView
import android.util.Log

private const val TAG = "AnnotationTypeSelector"

@Composable
fun AnnotationTypeSelector(
    glView: CustomModelSurfaceView?,
    onTypeSelected: (AnnotationType) -> Unit,
    currentType: AnnotationType,
    isSelectionActive: Boolean,
    modifier: Modifier = Modifier
) {
    // Only show annotation type buttons when in selection mode
    if (!isSelectionActive) return

    // Create a scroll state for horizontal scrolling
    val scrollState = rememberScrollState()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState)
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AnnotationType.values().forEach { annotationType ->
            // Create a button for each annotation type
            AnnotationTypeButton(
                type = annotationType,
                isSelected = annotationType == currentType,
                onClick = {
                    Log.d(TAG, "Annotation type selected: ${annotationType.displayName}")
                    onTypeSelected(annotationType)

                    // Apply the color to points in the selection cube
                    glView?.setAnnotationColor(
                        annotationType.defaultColor
                    )
                }
            )
        }
    }
}

/**
 * A button for a specific annotation type.
 */
@Composable
private fun AnnotationTypeButton(
    type: AnnotationType,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    // Convert Android Color int to Compose Color
    val buttonColor = Color(
        android.graphics.Color.red(type.defaultColor) / 255f,
        android.graphics.Color.green(type.defaultColor) / 255f,
        android.graphics.Color.blue(type.defaultColor) / 255f
    )

    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = if (isSelected) buttonColor else buttonColor.copy(alpha = 0.6f)
        ),
        modifier = Modifier.padding(horizontal = 4.dp)
    ) {
        Text(
            text = type.displayName,
            style = MaterialTheme.typography.labelMedium,
            color = if (isSelected) Color.White else Color.White.copy(alpha = 0.8f)
        )
    }
} 