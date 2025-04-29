package com.example.robotoperator.renderer

import android.content.Context
import android.opengl.GLSurfaceView

class CustomGLSurfaceView(context: Context) : GLSurfaceView(context) {
    private val renderer: GLRenderer

    init {
        // Create an OpenGL ES 3.0 context
        setEGLContextClientVersion(3)

        renderer = GLRenderer()
        
        // Set the Renderer for drawing on the GLSurfaceView
        setRenderer(renderer)
        
        // Render the view only when there is a change
        //todo =  do i need this ?
        renderMode = RENDERMODE_WHEN_DIRTY
    }
} 