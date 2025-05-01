package com.example.robotoperator.opengl

import android.opengl.GLES32
import android.opengl.Matrix
import com.example.robotoperator.util.Util.compileProgram
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.IntBuffer
import com.example.robotoperator.R

class CubeModel : ArrayModel() {
    private val cubeColor = floatArrayOf(0.6f, 0.8f, 1.0f, 0.3f) // Light blue with 30% opacity
    private var indexBuffer: IntBuffer? = null
    private var indexCount: Int = 0

    override fun setup(boundSize: Float) {
        // Define cube vertices (1x1x1 cube centered at origin)
        val vertices = floatArrayOf(
            // Front face
            -0.5f, -0.5f,  0.5f,  // Front-bottom-left  0
             0.5f, -0.5f,  0.5f,  // Front-bottom-right 1
             0.5f,  0.5f,  0.5f,  // Front-top-right    2
            -0.5f,  0.5f,  0.5f,  // Front-top-left     3
            
            // Back face
            -0.5f, -0.5f, -0.5f,  // Back-bottom-left   4
             0.5f, -0.5f, -0.5f,  // Back-bottom-right  5
             0.5f,  0.5f, -0.5f,  // Back-top-right     6
            -0.5f,  0.5f, -0.5f,  // Back-top-left      7
            
            // Top face
            -0.5f,  0.5f, -0.5f,  // Top-back-left      8
             0.5f,  0.5f, -0.5f,  // Top-back-right     9
             0.5f,  0.5f,  0.5f,  // Top-front-right    10
            -0.5f,  0.5f,  0.5f,  // Top-front-left     11
            
            // Bottom face
            -0.5f, -0.5f, -0.5f,  // Bottom-back-left   12
             0.5f, -0.5f, -0.5f,  // Bottom-back-right  13
             0.5f, -0.5f,  0.5f,  // Bottom-front-right 14
            -0.5f, -0.5f,  0.5f,  // Bottom-front-left  15
            
            // Right face
             0.5f, -0.5f, -0.5f,  // Right-bottom-back  16
             0.5f,  0.5f, -0.5f,  // Right-top-back     17
             0.5f,  0.5f,  0.5f,  // Right-top-front    18
             0.5f, -0.5f,  0.5f,  // Right-bottom-front 19
            
            // Left face
            -0.5f, -0.5f, -0.5f,  // Left-bottom-back   20
            -0.5f,  0.5f, -0.5f,  // Left-top-back      21
            -0.5f,  0.5f,  0.5f,  // Left-top-front     22
            -0.5f, -0.5f,  0.5f   // Left-bottom-front  23
        )

        // Define indices for drawing triangles
        val indices = intArrayOf(
            // Front face
            0, 1, 2, 0, 2, 3,
            // Back face
            4, 5, 6, 4, 6, 7,
            // Top face
            8, 9, 10, 8, 10, 11,
            // Bottom face
            12, 13, 14, 12, 14, 15,
            // Right face
            16, 17, 18, 16, 18, 19,
            // Left face
            20, 21, 22, 20, 22, 23
        )
        indexCount = indices.size

        // Create index buffer
        val ibb = ByteBuffer.allocateDirect(indices.size * 4)
        ibb.order(ByteOrder.nativeOrder())
        indexBuffer = ibb.asIntBuffer()
        indexBuffer!!.put(indices)
        indexBuffer!!.position(0)

        // Define normals for lighting
        val normals = floatArrayOf(
            // Front face
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,
            0.0f, 0.0f, 1.0f,
            
            // Back face
            0.0f, 0.0f, -1.0f,
            0.0f, 0.0f, -1.0f,
            0.0f, 0.0f, -1.0f,
            0.0f, 0.0f, -1.0f,
            
            // Top face
            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            0.0f, 1.0f, 0.0f,
            
            // Bottom face
            0.0f, -1.0f, 0.0f,
            0.0f, -1.0f, 0.0f,
            0.0f, -1.0f, 0.0f,
            0.0f, -1.0f, 0.0f,
            
            // Right face
            1.0f, 0.0f, 0.0f,
            1.0f, 0.0f, 0.0f,
            1.0f, 0.0f, 0.0f,
            1.0f, 0.0f, 0.0f,
            
            // Left face
            -1.0f, 0.0f, 0.0f,
            -1.0f, 0.0f, 0.0f,
            -1.0f, 0.0f, 0.0f,
            -1.0f, 0.0f, 0.0f
        )

        // Create vertex buffer
        var vbb = ByteBuffer.allocateDirect(vertices.size * BYTES_PER_FLOAT)
        vbb.order(ByteOrder.nativeOrder())
        vertexBuffer = vbb.asFloatBuffer()
        vertexBuffer!!.put(vertices)
        vertexBuffer!!.position(0)

        // Create normal buffer
        vbb = ByteBuffer.allocateDirect(normals.size * BYTES_PER_FLOAT)
        vbb.order(ByteOrder.nativeOrder())
        normalBuffer = vbb.asFloatBuffer()
        normalBuffer!!.put(normals)
        normalBuffer!!.position(0)

        // Create color buffer with solid color
        val colors = FloatArray(vertices.size / 3 * 4) // 4 components (RGBA) per vertex
        for (i in 0 until vertices.size / 3) {
            colors[i * 4] = cubeColor[0]     // R
            colors[i * 4 + 1] = cubeColor[1] // G
            colors[i * 4 + 2] = cubeColor[2] // B
            colors[i * 4 + 3] = cubeColor[3] // A
        }
        vbb = ByteBuffer.allocateDirect(colors.size * BYTES_PER_FLOAT)
        vbb.order(ByteOrder.nativeOrder())
        colorBuffer = vbb.asFloatBuffer()
        colorBuffer!!.put(colors)
        colorBuffer!!.position(0)

        useColorBuffer = true
        vertexCount = vertices.size / COORDS_PER_VERTEX

        // Set up the shader program
        if (GLES32.glIsProgram(glProgram)) {
            GLES32.glDeleteProgram(glProgram)
            glProgram = -1
        }
        glProgram = compileProgram(
            R.raw.model_vertex_color,
            R.raw.model_fragment_color,
            arrayOf("a_Position", "a_Normal", "a_Color")
        )

        // Initialize model matrix
        Matrix.setIdentityM(modelMatrix, 0)
        
        // Set cube size to 60% of the scene size
        val scale = boundSize * 0.6f
        Matrix.scaleM(modelMatrix, 0, scale, scale, scale)
        
        // Move cube slightly forward for better visibility
        Matrix.translateM(modelMatrix, 0, 0f, 0f, boundSize * 0.2f)
    }

    override fun draw(viewMatrix: FloatArray, projectionMatrix: FloatArray, light: Light) {
        // Save current OpenGL state
        val depthTestEnabled = GLES32.glIsEnabled(GLES32.GL_DEPTH_TEST)
        val blendEnabled = GLES32.glIsEnabled(GLES32.GL_BLEND)
        val currentBlendSrcFactor = IntArray(1)
        val currentBlendDstFactor = IntArray(1)
        GLES32.glGetIntegerv(GLES32.GL_BLEND_SRC_ALPHA, currentBlendSrcFactor, 0)
        GLES32.glGetIntegerv(GLES32.GL_BLEND_DST_ALPHA, currentBlendDstFactor, 0)
        
        // Configure OpenGL state for transparent cube
        GLES32.glEnable(GLES32.GL_DEPTH_TEST)
        GLES32.glDisable(GLES32.GL_CULL_FACE)
        GLES32.glEnable(GLES32.GL_BLEND)
        GLES32.glBlendFunc(GLES32.GL_SRC_ALPHA, GLES32.GL_ONE_MINUS_SRC_ALPHA)
        
        // Draw the cube
        super.draw(viewMatrix, projectionMatrix, light)
        
        // Restore previous OpenGL state
        if (!depthTestEnabled) {
            GLES32.glDisable(GLES32.GL_DEPTH_TEST)
        }
        if (!blendEnabled) {
            GLES32.glDisable(GLES32.GL_BLEND)
        } else {
            GLES32.glBlendFunc(currentBlendSrcFactor[0], currentBlendDstFactor[0])
        }
    }

    override fun drawFunc() {
        // Draw the cube using indexed triangles
        GLES32.glDrawElements(GLES32.GL_TRIANGLES, indexCount, GLES32.GL_UNSIGNED_INT, indexBuffer)
    }
} 