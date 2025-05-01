package com.example.robotoperator.ui

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import com.example.robotoperator.opengl.Model
import com.example.robotoperator.opengl.ply.PlyParser
import com.example.robotoperator.opengl.renderer.CustomModelSurfaceView

private const val TAG = "RobotOperator"

@Composable
fun GLView(modifier: Modifier = Modifier) {
    Log.d(TAG, "⭐ GLView composition started")
    val context = LocalContext.current
    var isSelectionMode by remember { mutableStateOf(false) }
    var glView: CustomModelSurfaceView? by remember { mutableStateOf(null) }

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

                // Show Coordinates button
                Button(
                    onClick = {
                        Log.d(TAG, "📍 Show coordinates button clicked")
                        glView?.showCubeCoordinates()
                    },
                    enabled = isSelectionMode,
                    modifier = Modifier.padding(horizontal = 8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Text(
                        text = "Show Coords",
                        style = MaterialTheme.typography.labelLarge
                    )
                }

                // Find Points button
                Button(
                    onClick = {
                        Log.d(TAG, "🔍 Find points button clicked")
                        glView?.findPointsInCube()
                    },
                    enabled = isSelectionMode,
                    modifier = Modifier.padding(horizontal = 8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.tertiary
                    )
                ) {
                    Text(
                        text = "Find Points",
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }
        )
    }
    Log.d(TAG, "✨ GLView composition completed")
} 