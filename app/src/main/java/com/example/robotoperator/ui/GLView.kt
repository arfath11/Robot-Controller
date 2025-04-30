package com.example.robotoperator.ui

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
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.example.robotoperator.opengl.Model
import com.example.robotoperator.opengl.ply.PlyParser
import com.example.robotoperator.opengl.renderer.CustomGLSurfaceView
import com.example.robotoperator.opengl.renderer.CustomModelSurfaceView

@Composable
fun GLView(modifier: Modifier = Modifier) {
    val context = LocalContext.current

    val model: Model? = remember {
        try {
            context.assets.open("scaniverse_model_binary.ply").use { stream ->
                PlyParser(stream) // Explicit cast to Model
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    Column(modifier = modifier.fillMaxSize()) {
        // OpenGL view takes all available space except for bottom bar
        AndroidView(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            factory = { context ->
                CustomModelSurfaceView(context,model).apply {
                    // Additional setup if needed
                }
            }
        )
        
        // Bottom bar with buttons
        BottomAppBar(
            modifier = Modifier.fillMaxWidth(),
            actions = {
                IconButton(onClick = { /* TODO: Handle reset */ }) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Reset view"
                    )
                }
                IconButton(onClick = { /* TODO: Handle edit */ }) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Edit model"
                    )
                }
                IconButton(onClick = { /* TODO: Handle add */ }) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add annotation"
                    )
                }
            }
        )
    }
} 