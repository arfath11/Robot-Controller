package com.example.robotoperator.opengl.renderer

import android.content.Context
import android.opengl.GLSurfaceView

class CustomGLSurfaceView(context: Context) : GLSurfaceView(context) {
    private val renderer: GLRenderer

    init {
        // Create an OpenGL ES 3.2 context
        setEGLContextClientVersion(3)
        
        // Configure context with additional flags for 3.2 support
        setEGLConfigChooser(8, 8, 8, 8, 16, 0)

        renderer = GLRenderer()
        
        // Set the Renderer for drawing on the GLSurfaceView
        setRenderer(renderer)
        
        // Render the view only when there is a change
        //todo =  do i need this ?
        renderMode = RENDERMODE_WHEN_DIRTY
    }
} 