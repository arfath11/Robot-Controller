package com.example.robotoperator

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.robotoperator.ui.GLView
import com.example.robotoperator.ui.theme.RobotOperatorTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RobotOperatorTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    GLView()
                }
            }
        }
    }
}