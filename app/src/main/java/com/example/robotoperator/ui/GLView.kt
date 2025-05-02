package com.example.robotoperator.ui

import android.util.Log
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.robotoperator.model.AnnotationType
import com.example.robotoperator.opengl.Model
import com.example.robotoperator.opengl.ply.PlyParser
import com.example.robotoperator.opengl.renderer.CustomModelSurfaceView
import com.example.robotoperator.ui.components.AnnotationTypeSelector
import androidx.compose.material.icons.filled.Check

private const val TAG = "RobotOperator"

@Composable
fun GLView(modifier: Modifier = Modifier) {
    Log.d(TAG, "⭐ GLView composition started")
    val context = LocalContext.current
    var isSelectionMode by remember { mutableStateOf(false) }
    var glView: CustomModelSurfaceView? by remember { mutableStateOf(null) }
    
    // Add state for current annotation type
    var currentAnnotationType by remember { mutableStateOf(AnnotationType.SPRAY_AREA) }

    // Track selected points
    var selectedPointsCount by remember { mutableStateOf(0) }
    
    // Create scroll state for the bottom row
    val bottomRowScrollState = rememberScrollState()

    DisposableEffect(Unit) {
        Log.d(TAG, "📱 GLView entered composition")
        onDispose {
            Log.d(TAG, "🗑️ GLView disposed")
        }
    }

    val model: Model? = remember {
        Log.d(TAG, "🔄 Loading 3D model from assets")
        try {
            context.assets.open("scaniverse_model_binary.ply").use { stream ->
                PlyParser(stream).also {
                    Log.d(TAG, "✅ Model loaded successfully")
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "❌ Failed to load model", e)
            null
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        AndroidView(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            factory = { context ->
                Log.d(TAG, "🏭 Creating new CustomModelSurfaceView")
                CustomModelSurfaceView(context, model).also { view ->
                    glView = view
                    Log.d(TAG, "🛠️ CustomModelSurfaceView initialized")
                }
            }
        )
        
        // Add the annotation type selector component
        AnnotationTypeSelector(
            glView = glView,
            onTypeSelected = { annotationType ->
                currentAnnotationType = annotationType
                Log.d(TAG, "Selected annotation type: ${annotationType.displayName}")
            },
            currentType = currentAnnotationType,
            isSelectionActive = isSelectionMode,
            modifier = Modifier.fillMaxWidth()
        )

        // Single bottom bar with both rotate and selection buttons
        BottomAppBar(
            modifier = Modifier.fillMaxWidth(),
            actions = {
                IconButton(onClick = {
                    Log.d(TAG, "🔄 Rotate button clicked")
                    glView?.rotate90()
                }) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Rotate 90°"
                    )
                }
                Button(
                    onClick = {
                        isSelectionMode = !isSelectionMode
                        glView?.setCubeSelected(isSelectionMode)
                        Log.d(TAG, "🎯 Cube selection mode changed to: $isSelectionMode")
                    },
                    modifier = Modifier.padding(horizontal = 8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSelectionMode)
                            MaterialTheme.colorScheme.primaryContainer
                        else
                            MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        text = if (isSelectionMode) "Exit Selection" else "Select Cube",
                        style = MaterialTheme.typography.labelLarge
                    )
                }

                Row(
                    modifier = Modifier.horizontalScroll(bottomRowScrollState)
                ) {
                    // Show Coordinates button
                    Button(
                        onClick = {
                            Log.d(TAG, "📍 Show coordinates button clicked")
                            glView?.showCubeCoordinates()
                        },
                        enabled = isSelectionMode,
                        modifier = Modifier.padding(horizontal = 4.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary
                        )
                    ) {
                        Text(
                            text = "Show Coords",
                            style = MaterialTheme.typography.labelMedium
                        )
                    }

                    // Find Points button
                    Button(
                        onClick = {
                            Log.d(TAG, "🔍 Find points button clicked")
                            glView?.findPointsInCube { points ->
                                selectedPointsCount = points.size
                            }
                        },
                        enabled = isSelectionMode,
                        modifier = Modifier.padding(horizontal = 4.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.tertiary
                        )
                    ) {
                        Text(
                            text = "Find Points",
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                    
                    // Save Annotation button
                    Button(
                        onClick = {
                            Log.d(TAG, "💾 Save annotation button clicked")
                            glView?.saveAnnotation(currentAnnotationType) { success ->
                                if (success) {
                                    Log.d(TAG, "✅ Annotation saved successfully")
                                } else {
                                    Log.e(TAG, "❌ Failed to save annotation")
                                }
                            }
                        },
                        enabled = isSelectionMode && selectedPointsCount > 0,
                        modifier = Modifier.padding(horizontal = 4.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Save Annotation",
                            modifier = Modifier.padding(end = 4.dp)
                        )
                        Text(
                            text = "Save ($selectedPointsCount points)",
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }
        )
    }
    Log.d(TAG, "✨ GLView composition completed")
} 