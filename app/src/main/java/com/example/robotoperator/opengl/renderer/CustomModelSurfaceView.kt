package com.example.robotoperator.opengl.renderer

import android.content.Context
import android.graphics.PointF
import android.opengl.GLSurfaceView
import android.util.Log
import android.view.MotionEvent
import com.example.robotoperator.util.Util.pxToDp
import com.example.robotoperator.opengl.Model
import kotlin.math.sqrt

private const val TAG = "RobotOperator"

class CustomModelSurfaceView(context: Context, model: Model?) : GLSurfaceView(context) {
    var renderer: CustomModelRenderer
    private var previousX = 0f
    private var previousY = 0f
    private val pinchStartPoint = PointF()
    private var pinchStartDistance = 0.0f
    private var touchMode = TOUCH_NONE

    init {
        Log.d(TAG, "🎨 Initializing CustomModelSurfaceView")
        setEGLContextClientVersion(3)
        
        // Configure context with additional flags for 3.2 support
        setEGLConfigChooser(8, 8, 8, 8, 16, 0)
        
        renderer = CustomModelRenderer(model)
        setRenderer(renderer)
        renderMode = RENDERMODE_WHEN_DIRTY
        Log.d(TAG, "🎯 Renderer set and mode configured")
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        when (event.action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                Log.v(TAG, "👇 Touch DOWN at x=${event.x}, y=${event.y}")
                previousX = event.x
                previousY = event.y
            }
            MotionEvent.ACTION_MOVE -> {
                Log.v(TAG, "👆 Touch MOVE with ${event.pointerCount} pointers")
                if (event.pointerCount == 1) {
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
                    Log.v(TAG, "🔄 Rotating dx=${pxToDp(dx)}, dy=${pxToDp(dy)}")
                } else if (event.pointerCount == 2) {
                    Log.v(TAG, "🤏 Pinch gesture detected")
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
                        Log.v(TAG, "📏 Zooming scale=$pinchScale, translation dx=${pxToDp(dx)}, dy=${pxToDp(dy)}")
                    }
                }
                requestRender()
            }
            MotionEvent.ACTION_UP -> {
                Log.v(TAG, "✋ Touch UP")
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

    fun rotateY180() {
        Log.d(TAG, "🔄 Executing 180-degree Y rotation")
        renderer.rotateY180()
        requestRender()  // Explicitly request a single render
    }

    companion object {
        private const val TOUCH_NONE = 0
        private const val TOUCH_ROTATE = 1
        private const val TOUCH_ZOOM = 2
    }
}