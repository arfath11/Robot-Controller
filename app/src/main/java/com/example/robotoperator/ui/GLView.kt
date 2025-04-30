package com.example.robotoperator.ui

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import com.example.robotoperator.opengl.Model
import com.example.robotoperator.opengl.ply.PlyParser
import com.example.robotoperator.opengl.renderer.CustomModelSurfaceView

private const val TAG = "RobotOperator"

@Composable
fun GLView(modifier: Modifier = Modifier) {
    Log.d(TAG, "⭐ GLView composition started")
    val context = LocalContext.current

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
                CustomModelSurfaceView(context, model).apply {
                    Log.d(TAG, "🛠️ CustomModelSurfaceView initialized")
                }
            }
        )
        
        BottomAppBar(
            modifier = Modifier.fillMaxWidth(),
            actions = {
                IconButton(onClick = { 
                    Log.d(TAG, "🔄 Reset button clicked")
                    /* TODO: Handle reset */ 
                }) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Reset view"
                    )
                }
                IconButton(onClick = { 
                    Log.d(TAG, "✏️ Edit button clicked")
                    /* TODO: Handle edit */ 
                }) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit model"
                    )
                }
                IconButton(onClick = { 
                    Log.d(TAG, "➕ Add button clicked")
                    /* TODO: Handle add */ 
                }) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add annotation"
                    )
                }
            }
        )
    }
    Log.d(TAG, "✨ GLView composition completed")
} 