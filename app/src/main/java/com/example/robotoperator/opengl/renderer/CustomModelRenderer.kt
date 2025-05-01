package com.example.robotoperator.opengl.renderer

import android.opengl.GLES32
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.util.Log
import com.example.robotoperator.opengl.Floor
import com.example.robotoperator.opengl.Light
import com.example.robotoperator.opengl.Model
import com.example.robotoperator.opengl.CubeModel
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

private const val TAG = "RobotOperator"

class CustomModelRenderer(private val model: Model?) : GLSurfaceView.Renderer {
    private val light = Light(floatArrayOf(0.0f, 0.0f, LIGHT_POSITION_Z, 1.0f))
    private val floor = Floor()
    private val cube = CubeModel()
    
    private var isCubeSelected = false

    private val projectionMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)

    private var rotateAngleX = 0f
    private var rotateAngleY = 0f
    private var translateX = 0f
    private var translateY = 0f
    private var translateZ = 0f

    init {
        Log.d(TAG, "🎮 Initializing CustomModelRenderer with model: ${model != null}")
    }

    override fun onDrawFrame(unused: GL10) {
        Log.v(TAG, "🎨 Drawing frame")
        GLES32.glClear(GLES32.GL_COLOR_BUFFER_BIT or GLES32.GL_DEPTH_BUFFER_BIT)
        
        floor.draw(viewMatrix, projectionMatrix, light)
        model?.draw(viewMatrix, projectionMatrix, light)
        cube.draw(viewMatrix, projectionMatrix, light)
    }

    override fun onSurfaceCreated(unused: GL10, config: EGLConfig) {
        Log.d(TAG, "🆕 Surface created")
        GLES32.glClearColor(0.2f, 0.2f, 0.2f, 1f)
        GLES32.glEnable(GLES32.GL_CULL_FACE)
        GLES32.glEnable(GLES32.GL_DEPTH_TEST)
        GLES32.glEnable(GLES32.GL_BLEND)
        GLES32.glBlendFunc(GLES32.GL_SRC_ALPHA, GLES32.GL_ONE_MINUS_SRC_ALPHA)

        floor.setup(FLOOR_SIZE)
        cube.setup(CUBE_SIZE)
        model?.let {
            it.setup(MODEL_BOUND_SIZE)
            floor.setOffsetY(it.floorOffset)
            Log.d(TAG, "✅ Model and floor setup completed")
        }
    }

    fun setCubeSelected(selected: Boolean) {
        isCubeSelected = selected
    }

    fun isCubeSelected(): Boolean = isCubeSelected

    fun moveCube(dx: Float, dy: Float, dz: Float) {
        if (!isCubeSelected) return
        Matrix.translateM(cube.modelMatrix, 0, dx, dy, dz)
    }

    fun getRotationY(): Float = rotateAngleY

    fun setRotationY(angle: Float) {
        rotateAngleY = angle
        updateViewMatrix()
    }

    fun rotate90Degrees() {
        Log.d(TAG, "🔄 Rotating 90 degrees from current angle Y: $rotateAngleY")
        rotateAngleY = (rotateAngleY + 90f) % 360f
        updateViewMatrix()
        Log.d(TAG, "✅ Rotation complete. New angle Y: $rotateAngleY")
    }

    fun translate(dx: Float, dy: Float, dz: Float) {
        Log.v(TAG, "📏 Translating dx=$dx, dy=$dy, dz=$dz")
        val translateScaleFactor = MODEL_BOUND_SIZE / 200f
        translateX += dx * translateScaleFactor
        translateY += dy * translateScaleFactor
        if (dz != 0f) {
            translateZ /= dz
        }
        updateViewMatrix()
    }

    fun rotate(aX: Float, aY: Float) {
        Log.v(TAG, "🔄 Rotating aX=$aX, aY=$aY")
        val rotateScaleFactor = 0.5f
        rotateAngleX -= aX * rotateScaleFactor
        rotateAngleY += aY * rotateScaleFactor
        updateViewMatrix()
    }

    fun rotateY180() {
        Log.d(TAG, "🔄 Rotating model 180 degrees around Y axis")
        rotateAngleY += 180f
        updateViewMatrix()
    }

    private fun updateViewMatrix() {
        Log.v(TAG, "🔄 Updating view matrix")
        Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, translateZ, 0f, 0f, 0f, 0f, 1.0f, 0.0f)
        Matrix.translateM(viewMatrix, 0, -translateX, -translateY, 0f)
        Matrix.rotateM(viewMatrix, 0, rotateAngleX, 1f, 0f, 0f)
        Matrix.rotateM(viewMatrix, 0, rotateAngleY, 0f, 1f, 0f)
    }

    override fun onSurfaceChanged(unused: GL10, width: Int, height: Int) {
        Log.d(TAG, "📐 Surface changed: width=$width, height=$height")
        GLES32.glViewport(0, 0, width, height)
        val ratio = width.toFloat() / height
        Matrix.frustumM(projectionMatrix, 0, -ratio, ratio, -1f, 1f, Z_NEAR, Z_FAR)

        // initialize the view matrix
        rotateAngleX = 0f
        rotateAngleY = 0f
        translateX = 0f
        translateY = 0f
        translateZ = INITIAL_CAMERA_Z
        updateViewMatrix()

        // Set light matrix before doing any other transforms on the view matrix
        light.applyViewMatrix(viewMatrix)

        // By default, rotate the model towards the user a bit
        rotateAngleX = INITIAL_ROTATION_X
        rotateAngleY = INITIAL_ROTATION_Y
        updateViewMatrix()
        Log.d(TAG, "🎯 Initial view setup completed")
    }

    companion object {
        private const val MODEL_BOUND_SIZE = 6f  // Base unit for model scaling
        private const val FLOOR_SIZE = 7.2f      // 1.2 * MODEL_BOUND_SIZE
        private const val CUBE_SIZE = 2.4f       // 0.4 * MODEL_BOUND_SIZE
        private const val Z_NEAR = 2f            // Near clipping plane
        private const val Z_FAR = 60f            // Far clipping plane
        private const val LIGHT_POSITION_Z = 60f // Light distance
        private const val INITIAL_CAMERA_Z = -15f // Initial camera distance
        private const val INITIAL_ROTATION_X = -20.0f // Initial X rotation
        private const val INITIAL_ROTATION_Y = 20.0f  // Initial Y rotation
    }
}
