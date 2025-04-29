package com.example.robotoperator

import android.app.ActivityManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.robotoperator.ui.theme.RobotOperatorTheme

class MainActivity : ComponentActivity() {
    private val TAG = "OpenGLCheck"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Check OpenGL ES version
        checkOpenGLVersion()
        
        enableEdgeToEdge()
        setContent {
            RobotOperatorTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    private fun checkOpenGLVersion() {
        val activityManager = getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val configurationInfo = activityManager.deviceConfigurationInfo
        
        // Get the OpenGL ES version as a double
        val openGLVersion = configurationInfo.glEsVersion.toDouble()
        
        Log.d(TAG, "Device OpenGL ES version: $openGLVersion")
        
        // Check if the device supports OpenGL ES 3.2
        val supportsOpenGLES32 = openGLVersion >= 3.2
        Log.d(TAG, "Supports OpenGL ES 3.2: $supportsOpenGLES32")
        
        // Get supported OpenGL extensions
        val extensions = android.opengl.GLES32.glGetString(android.opengl.GLES32.GL_EXTENSIONS)
        Log.d(TAG, "Supported OpenGL extensions: $extensions")
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    RobotOperatorTheme {
        Greeting("Android")
    }
}