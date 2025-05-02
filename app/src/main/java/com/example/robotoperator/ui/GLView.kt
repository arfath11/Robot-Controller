package com.example.robotoperator.ui

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Check
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.robotoperator.R
import com.example.robotoperator.domain.model.PointCloud
import com.example.robotoperator.model.AnnotationType
import com.example.robotoperator.opengl.Model
import com.example.robotoperator.opengl.ply.PlyParser
import com.example.robotoperator.opengl.renderer.CustomModelSurfaceView
import com.example.robotoperator.ui.components.AnnotationTypeSelector
import com.example.robotoperator.ui.viewmodel.GLViewModel

private const val TAG = "RobotOperator"

@Composable
fun GLView(
    modifier: Modifier = Modifier,
    viewModel: GLViewModel = hiltViewModel()
) {
    Log.d(TAG, "⭐ GLView composition started")
    val context = LocalContext.current
    var isSelectionMode by remember { mutableStateOf(false) }
    var glView: CustomModelSurfaceView? by remember { mutableStateOf(null) }
    // Add state for current annotation type
    var currentAnnotationType by remember { mutableStateOf(AnnotationType.SPRAY_AREA) }

    // Track selected points
    var selectedPointsCount by remember { mutableStateOf(0) }
    
    // Store the found point cloud for later saving
    var currentPointCloud by remember { mutableStateOf<PointCloud?>(null) }

    // Create scroll state for the bottom row
    val bottomRowScrollState = rememberScrollState()


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
                        
                        // Reset point cloud when exiting selection mode
                        if (!isSelectionMode) {
                            currentPointCloud = null
                            selectedPointsCount = 0
                        }
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
                        text = if (isSelectionMode) "Exit" else "Select Cube",
                        style = MaterialTheme.typography.labelLarge
                    )
                }

                Row(
                    modifier = Modifier.horizontalScroll(bottomRowScrollState)
                ) {

                    // Find Points button
                    Button(
                        onClick = {
                            Log.d(TAG, "🔍 Find Points button clicked")
                            
                            // Reset the current point cloud
                            currentPointCloud = null
                            selectedPointsCount = 0
                            
                            // Find points but don't save yet
                            glView?.findPointsInCube { pointCloud ->
                                // Store the point cloud for later saving
                                currentPointCloud = pointCloud
                                if (pointCloud != null && pointCloud.points.isNotEmpty()) {
                                    selectedPointsCount = pointCloud.points.size
                                    Log.d(TAG, "Found ${pointCloud.points.size} points with type ${pointCloud.annotationType.displayName}")
                                    Toast.makeText(context, "Found ${pointCloud.points.size} points", Toast.LENGTH_SHORT).show()
                                } else {
                                    Log.e(TAG, "❌ No points found to store")
                                    Toast.makeText(context, "No points found in selection", Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        enabled = isSelectionMode,
                        modifier = Modifier.padding(horizontal = 4.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.tertiary
                        )
                    ) {
                        Text(
                            text = stringResource(R.string.mark),
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                    
                    // Save Points button - saves to database
                    Button(
                        onClick = {
                            Log.d(TAG, "💾 Save Points button clicked")
                            
                            // Save the stored point cloud to the database
                            currentPointCloud?.let { pointCloud ->
                                if (pointCloud.points.isNotEmpty()) {
                                    Log.d(TAG, "Saving ${pointCloud.points.size} points with type ${pointCloud.annotationType.displayName}")
                                    
                                    try {
                                        // Call the viewModel to save the points
                                        viewModel.savePointCloud(pointCloud.annotationType, pointCloud.points)
                                        Toast.makeText(context, "Saving ${pointCloud.points.size} points to database", Toast.LENGTH_SHORT).show()
                                    } catch (e: Exception) {
                                        Log.e(TAG, "❌ Error while trying to save points: ${e.message}", e)
                                        Toast.makeText(context, "Error saving points: ${e.message}", Toast.LENGTH_LONG).show()
                                    }
                                } else {
                                    Log.e(TAG, "❌ No points available to save")
                                    Toast.makeText(context, "No points to save", Toast.LENGTH_SHORT).show()
                                }
                            } ?: run {
                                Log.e(TAG, "❌ No point cloud available - find points first")
                                Toast.makeText(context, "No points found yet - use Find Points first", Toast.LENGTH_SHORT).show()
                            }
                        },
                        enabled = isSelectionMode && currentPointCloud != null && (currentPointCloud?.points?.isNotEmpty() == true),
                        modifier = Modifier.padding(horizontal = 4.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Text(
                            text = stringResource(R.string.save),
                            style = MaterialTheme.typography.labelMedium
                        )
                    }

                    Button(
                        onClick = { viewModel.deleteAllPoints() },
                        enabled = isSelectionMode,
                        modifier = Modifier.padding(horizontal = 4.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary
                        )
                    ) {
                        Text(
                            text = "Clear DB",
                            style = MaterialTheme.typography.labelMedium
                        )
                    }

                    Button(
                        onClick = { viewModel.deleteAllPoints() },
                        enabled = isSelectionMode,
                        modifier = Modifier.padding(horizontal = 4.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondary
                        )
                    ) {
                        Text(
                            text = "Load Annotations",
                            style = MaterialTheme.typography.labelMedium
                        )
                    }

                    Button(
                        onClick = {
                           val pointClouds = viewModel.getAllPointClouds()
                        },
                        modifier = Modifier.padding(horizontal = 4.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.tertiary
                        )
                    ) {
                        Text(
                            text = "Load Annotations",
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                }
            }
        )
    }
}