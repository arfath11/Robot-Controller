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
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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

private const val TAG = "GLView"

@Composable
fun GLView(
    modifier: Modifier = Modifier,
    viewModel: GLViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    var isSelectionMode by remember { mutableStateOf(false) }
    var currentAnnotationType by remember { mutableStateOf(AnnotationType.SPRAY_AREA) }
    var selectedPointsCount by remember { mutableStateOf(0) }
    var currentPointCloud by remember { mutableStateOf<PointCloud?>(null) }
    var glView: CustomModelSurfaceView? by remember { mutableStateOf(null) }

    Column(modifier = modifier.fillMaxSize()) {
        // 3D Model View
        GLModelView(
            onViewCreated = { view -> glView = view },
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        )

        // Annotation Type Selector
        AnnotationTypeSelector(
            glView = glView,
            onTypeSelected = { annotationType ->
                currentAnnotationType = annotationType
            },
            currentType = currentAnnotationType,
            isSelectionActive = isSelectionMode,
            modifier = Modifier.fillMaxWidth()
        )

        // Bottom Action Bar
        GLBottomActionBar(
            isSelectionMode = isSelectionMode,
            onSelectionModeChanged = { newMode ->
                isSelectionMode = newMode
                glView?.setCubeSelected(newMode)
                
                if (!newMode) {
                    currentPointCloud = null
                    selectedPointsCount = 0
                }
            },
            onRotateClicked = { glView?.rotate90() },
            onFindPoints = {
                findPoints(
                    glView = glView,
                    onPointCloudFound = { pointCloud ->
                        currentPointCloud = pointCloud
                        selectedPointsCount = pointCloud?.points?.size ?: 0
                    },
                    context = context
                )
            },
            onSavePoints = {
                savePoints(
                    currentPointCloud = currentPointCloud,
                    viewModel = viewModel,
                    context = context
                )
            },
            onClearDatabase = { viewModel.deleteAllPoints() },
            onLoadAnnotations = {
                loadAnnotations(
                    viewModel = viewModel,
                    glView = glView, 
                    context = context
                )
            },
            currentPointCloud = currentPointCloud,
            isSelectionEnabled = isSelectionMode
        )
    }
}

@Composable
private fun GLModelView(
    onViewCreated: (CustomModelSurfaceView) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val model: Model? = remember {
        try {
            context.assets.open("scaniverse_model_binary.ply").use { stream ->
                PlyParser(stream)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load model", e)
            null
        }
    }

    AndroidView(
        modifier = modifier,
        factory = { ctx ->
            CustomModelSurfaceView(ctx, model).also { view ->
                onViewCreated(view)
            }
        }
    )
}

@Composable
private fun GLBottomActionBar(
    isSelectionMode: Boolean,
    onSelectionModeChanged: (Boolean) -> Unit,
    onRotateClicked: () -> Unit,
    onFindPoints: () -> Unit,
    onSavePoints: () -> Unit,
    onClearDatabase: () -> Unit,
    onLoadAnnotations: () -> Unit,
    currentPointCloud: PointCloud?,
    isSelectionEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    val bottomRowScrollState = rememberScrollState()

    BottomAppBar(
        modifier = modifier,
        actions = {
            // Rotate Button
            IconButton(onClick = onRotateClicked) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Rotate 90°"
                )
            }

            // Selection Mode Button
            SelectionModeButton(
                isSelectionMode = isSelectionMode,
                onSelectionModeChanged = onSelectionModeChanged
            )

            // Action Buttons
            Row(
                modifier = Modifier.horizontalScroll(bottomRowScrollState)
            ) {
                AnnotationActionButtons(
                    onFindPoints = onFindPoints,
                    onSavePoints = onSavePoints,
                    onClearDatabase = onClearDatabase,
                    onLoadAnnotations = onLoadAnnotations,
                    isSelectionEnabled = isSelectionEnabled,
                    hasPointsSelected = currentPointCloud != null && currentPointCloud.points.isNotEmpty()
                )
            }
        }
    )
}

@Composable
private fun SelectionModeButton(
    isSelectionMode: Boolean,
    onSelectionModeChanged: (Boolean) -> Unit
) {
    Button(
        onClick = { onSelectionModeChanged(!isSelectionMode) },
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
}

@Composable
private fun AnnotationActionButtons(
    onFindPoints: () -> Unit,
    onSavePoints: () -> Unit,
    onClearDatabase: () -> Unit,
    onLoadAnnotations: () -> Unit,
    isSelectionEnabled: Boolean,
    hasPointsSelected: Boolean
) {
    // Find Points Button
    Button(
        onClick = onFindPoints,
        enabled = isSelectionEnabled,
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
    
    // Save Points Button
    Button(
        onClick = onSavePoints,
        enabled = isSelectionEnabled && hasPointsSelected,
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

    // Clear Database Button
    Button(
        onClick = onClearDatabase,
        enabled = isSelectionEnabled,
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

    // Load Annotations Button
    Button(
        onClick = onLoadAnnotations,
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

private fun findPoints(
    glView: CustomModelSurfaceView?,
    onPointCloudFound: (PointCloud?) -> Unit,
    context: android.content.Context
) {
    glView?.findPointsInCube { pointCloud ->
        onPointCloudFound(pointCloud)
        
        if (pointCloud != null && pointCloud.points.isNotEmpty()) {
            Toast.makeText(context, "Found ${pointCloud.points.size} points", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "No points found in selection", Toast.LENGTH_SHORT).show()
        }
    }
}

private fun savePoints(
    currentPointCloud: PointCloud?,
    viewModel: GLViewModel,
    context: android.content.Context
) {
    currentPointCloud?.let { pointCloud ->
        if (pointCloud.points.isNotEmpty()) {
            try {
                viewModel.savePointCloud(pointCloud.annotationType, pointCloud.points)
                Toast.makeText(context, "Saving ${pointCloud.points.size} points to database", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                Log.e(TAG, "Error while trying to save points", e)
                Toast.makeText(context, "Error saving points: ${e.message}", Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(context, "No points to save", Toast.LENGTH_SHORT).show()
        }
    } ?: run {
        Toast.makeText(context, "No points found yet - use Find Points first", Toast.LENGTH_SHORT).show()
    }
}

private fun loadAnnotations(
    viewModel: GLViewModel,
    glView: CustomModelSurfaceView?,
    context: android.content.Context
) {
    Toast.makeText(context, "Loading annotations...", Toast.LENGTH_SHORT).show()
    
    viewModel.getAllPointClouds { pointClouds ->
        if (pointClouds.isEmpty()) {
            Toast.makeText(context, "No annotations found in database", Toast.LENGTH_SHORT).show()
            return@getAllPointClouds
        }
        
        val totalPoints = pointClouds.sumOf { it.points.size }
        
        try {
            glView?.loadAnnotation(pointClouds)
            Toast.makeText(
                context, 
                "Loaded $totalPoints points across ${pointClouds.size} annotation types", 
                Toast.LENGTH_SHORT
            ).show()
        } catch (e: Exception) {
            Log.e(TAG, "Error loading annotations", e)
            Toast.makeText(
                context,
                "Error loading annotations: ${e.message}",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}