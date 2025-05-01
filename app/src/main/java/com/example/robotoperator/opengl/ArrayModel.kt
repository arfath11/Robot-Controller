package com.example.robotoperator.opengl
import android.opengl.GLES32
import android.opengl.Matrix
import com.example.robotoperator.util.Util.compileProgram
import java.nio.FloatBuffer
import com.example.robotoperator.R


open class ArrayModel : Model() {
    // Vertices, normals will be populated by subclasses
    var vertexCount = 0
        protected set

    override var vertexBuffer: FloatBuffer? = null
    protected var normalBuffer: FloatBuffer? = null
    var colorBuffer: FloatBuffer? = null
    protected var useColorBuffer = false

    override fun setup(boundSize: Float) {
        if (GLES32.glIsProgram(glProgram)) {
            GLES32.glDeleteProgram(glProgram)
            glProgram = -1
        }
        glProgram = if (useColorBuffer) {
            compileProgram(R.raw.model_vertex_color, R.raw.model_fragment_color, arrayOf("a_Position", "a_Normal", "a_Color"))
        } else {
            compileProgram(R.raw.model_vertex, R.raw.single_light_fragment, arrayOf("a_Position", "a_Normal"))
        }
        super.setup(boundSize)
    }

    override fun draw(viewMatrix: FloatArray, projectionMatrix: FloatArray, light: Light) {
        if (vertexBuffer == null || normalBuffer == null) {
            return
        }
        GLES32.glUseProgram(glProgram)

        val mvpMatrixHandle = GLES32.glGetUniformLocation(glProgram, "u_MVP")
        val positionHandle = GLES32.glGetAttribLocation(glProgram, "a_Position")
        val normalHandle = GLES32.glGetAttribLocation(glProgram, "a_Normal")
        val lightPosHandle = GLES32.glGetUniformLocation(glProgram, "u_LightPos")
        val ambientColorHandle = GLES32.glGetUniformLocation(glProgram, "u_ambientColor")
        val diffuseColorHandle = GLES32.glGetUniformLocation(glProgram, "u_diffuseColor")
        val specularColorHandle = GLES32.glGetUniformLocation(glProgram, "u_specularColor")
        var colorHandle = -1

        GLES32.glEnableVertexAttribArray(positionHandle)
        GLES32.glVertexAttribPointer(positionHandle, COORDS_PER_VERTEX, GLES32.GL_FLOAT, false,
                VERTEX_STRIDE, vertexBuffer)
        GLES32.glEnableVertexAttribArray(normalHandle)
        GLES32.glVertexAttribPointer(normalHandle, COORDS_PER_VERTEX, GLES32.GL_FLOAT, false,
                VERTEX_STRIDE, normalBuffer)

        if (colorBuffer != null) {
            colorHandle = GLES32.glGetAttribLocation(glProgram, "a_Color")
            GLES32.glEnableVertexAttribArray(colorHandle)
            GLES32.glVertexAttribPointer(colorHandle, 4, GLES32.GL_FLOAT, false, 4 * BYTES_PER_FLOAT, colorBuffer)
        }

        Matrix.multiplyMM(mvMatrix, 0, viewMatrix, 0, modelMatrix, 0)
        Matrix.multiplyMM(mvpMatrix, 0, projectionMatrix, 0, mvMatrix, 0)

        GLES32.glUniformMatrix4fv(mvpMatrixHandle, 1, false, mvpMatrix, 0)
        GLES32.glUniform3fv(lightPosHandle, 1, light.positionInEyeSpace, 0)
        if (ambientColorHandle >= 0) {
            GLES32.glUniform4fv(ambientColorHandle, 1, light.ambientColor, 0)
        }
        if (diffuseColorHandle >= 0) {
            GLES32.glUniform4fv(diffuseColorHandle, 1, light.diffuseColor, 0)
        }
        GLES32.glUniform4fv(specularColorHandle, 1, light.specularColor, 0)

        drawFunc()

        if (colorHandle >= 0) {
            GLES32.glDisableVertexAttribArray(colorHandle)
        }
        GLES32.glDisableVertexAttribArray(normalHandle)
        GLES32.glDisableVertexAttribArray(positionHandle)
    }

    protected open fun drawFunc() {
        GLES32.glDrawArrays(GLES32.GL_TRIANGLES, 0, vertexCount)
    }

    companion object {
        const val BYTES_PER_FLOAT = 4
        const val COORDS_PER_VERTEX = 3
        const val VERTEX_STRIDE = COORDS_PER_VERTEX * BYTES_PER_FLOAT
        const val INPUT_BUFFER_SIZE = 0x10000
    }
}
