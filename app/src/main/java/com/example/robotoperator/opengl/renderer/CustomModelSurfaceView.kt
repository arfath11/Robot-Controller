package com.example.robotoperator.opengl.renderer

import android.content.Context
import android.graphics.PointF
import android.opengl.GLSurfaceView
import android.util.Log
import android.view.MotionEvent
import com.example.robotoperator.util.Util.pxToDp
import com.example.robotoperator.opengl.Model
import com.example.robotoperator.model.AnnotationType
import kotlin.math.sqrt

private const val TAG = "RobotOperator"

class CustomModelSurfaceView(context: Context, model: Model?) : GLSurfaceView(context) {
    var renderer: CustomModelRenderer
    private var previousX = 0f
    private var previousY = 0f
    private val pinchStartPoint = PointF()
    private var pinchStartDistance = 0.0f
    private var touchMode = TOUCH_NONE
    private var currentAnnotationColor: Int = android.graphics.Color.RED
    
    // Store selected points
    private var selectedPoints = mutableListOf<FloatArray>()
    
    // Create annotation manager

    init {
        Log.d(TAG, "🎨 Initializing CustomModelSurfaceView")
        setEGLContextClientVersion(3)
        
        // Configure context with additional flags for 3.2 support
        setEGLConfigChooser(8, 8, 8, 8, 16, 0)
        
        renderer = CustomModelRenderer(model)
        setRenderer(renderer)
        // Only render when there is a change in the drawing data
        renderMode = RENDERMODE_WHEN_DIRTY
        Log.d(TAG, "🎯 Renderer set and mode configured")
    }

    // Add method to handle cube selection
    fun setCubeSelected(selected: Boolean) {
        queueEvent {
            renderer.setCubeSelected(selected)
            if (!selected) {
                // Clear selected points when exiting selection mode
                selectedPoints.clear()
            }
            requestRender()
        }
    }

    // Add method to set annotation color
    fun setAnnotationColor(color: Int) {
        currentAnnotationColor = color
        Log.d(TAG, "Setting annotation color: $color")
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                previousX = event.x
                previousY = event.y
            }
            MotionEvent.ACTION_MOVE -> {
                if (event.pointerCount == 1) {
                    if (renderer.isCubeSelected()) {
                        // Move the cube if it's selected
                        val x = event.x
                        val y = event.y
                        val dx = x - previousX
                        val dy = y - previousY
                        previousX = x
                        previousY = y
                        // Scale movement to match finger movement 1:1
                        val moveScale = 0.01f 
                        renderer.moveCube(-dx * moveScale, -dy * moveScale, 0f)
                    } else {
                        // Handle camera rotation when cube is not selected
                        if (touchMode != TOUCH_ROTATE) {
                            previousX = event.x
                            previousY = event.y
                        }
                        touchMode = TOUCH_ROTATE
                        val x = event.x
                        val y = event.y
                        val dx = x - previousX
                        val dy = y - previousY
                        previousX = x
                        previousY = y
                        renderer.rotate(pxToDp(dy), pxToDp(dx))
                    }
                } else if (event.pointerCount == 2 && !renderer.isCubeSelected()) {
                    // Only handle pinch zoom when cube is not selected
                    if (touchMode != TOUCH_ZOOM) {
                        pinchStartDistance = getPinchDistance(event)
                        getPinchCenterPoint(event, pinchStartPoint)
                        previousX = pinchStartPoint.x
                        previousY = pinchStartPoint.y
                        touchMode = TOUCH_ZOOM
                    } else {
                        val pt = PointF()
                        getPinchCenterPoint(event, pt)
                        val dx = pt.x - previousX
                        val dy = pt.y - previousY
                        previousX = pt.x
                        previousY = pt.y
                        val pinchScale = getPinchDistance(event) / pinchStartDistance
                        pinchStartDistance = getPinchDistance(event)
                        renderer.translate(pxToDp(dx), pxToDp(dy), pinchScale)
                    }
                }
                requestRender()
            }
            MotionEvent.ACTION_UP -> {
                pinchStartPoint.x = 0.0f
                pinchStartPoint.y = 0.0f
                touchMode = TOUCH_NONE
            }
        }
        return true
    }

    override fun onAttachedToWindow() {
        Log.d(TAG, "📌 onAttachedToWindow")
        super.onAttachedToWindow()
    }

    override fun onDetachedFromWindow() {
        Log.d(TAG, "🔌 onDetachedFromWindow")
        super.onDetachedFromWindow()
    }

    override fun onPause() {
        Log.d(TAG, "⏸️ onPause")
        super.onPause()
    }

    override fun onResume() {
        Log.d(TAG, "▶️ onResume")
        super.onResume()
    }

    private fun getPinchDistance(event: MotionEvent): Float {
        val x = event.getX(0) - event.getX(1)
        val y = event.getY(0) - event.getY(1)
        return sqrt((x * x + y * y).toDouble()).toFloat()
    }

    private fun getPinchCenterPoint(event: MotionEvent, pt: PointF) {
        pt.x = (event.getX(0) + event.getX(1)) * 0.5f
        pt.y = (event.getY(0) + event.getY(1)) * 0.5f
    }

    fun rotate90() {
        Log.d(TAG, "🔄 Requesting 90-degree rotation")
        // Queue the rotation operation to run on the GL thread
        //todo can be done better
        queueEvent {
            Log.d(TAG, "🎯 Executing rotation on GL thread")
            renderer.rotate90Degrees()
            requestRender()
        }
    }

    // Function to show cube coordinates
    fun showCubeCoordinates() {
        queueEvent {
            if (renderer.isCubeSelected()) {
                renderer.cube.getWorldSpaceCoordinates()
            } else {
                Log.d(TAG, "Cannot show coordinates - cube is not selected")
            }
        }
    }

    // Function to find points inside the cube with callback for the count
    fun findPointsInCube(onPointsFound: (List<FloatArray>) -> Unit = {}) {
        queueEvent {
            if (renderer.isCubeSelected()) {
                renderer.model?.let { model ->
                    // Get cube corners in world space
                    val cubeCorners = renderer.cube.getWorldSpaceCoordinates()
                    
                    // Find points inside cube and color them with the current annotation color
                    val points = renderer.cube.findPointsInCube(model, currentAnnotationColor)
                    Log.d(TAG, "Found ${points.size} points inside the cube with color: $currentAnnotationColor")
                    
                    // Store the points for later use
                    selectedPoints.clear()
                    selectedPoints.addAll(points)
                    
                    // Call the callback with the points
                    onPointsFound(points)
                } ?: Log.d(TAG, "No model available for point detection")
            } else {
                Log.d(TAG, "Cannot find points - cube is not selected")
                onPointsFound(emptyList())
            }
        }
        requestRender()
    }
    
    // Function to save the current annotation
    fun saveAnnotation(type: AnnotationType, onSaveComplete: (Boolean) -> Unit = {}) {
        queueEvent {

        }
    }

    companion object {
        private const val TOUCH_NONE = 0
        private const val TOUCH_ROTATE = 1
        private const val TOUCH_ZOOM = 2
        private const val TOUCH_MOVE_CUBE = 3
    }
}