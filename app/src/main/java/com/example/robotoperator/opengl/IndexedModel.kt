package com.example.robotoperator.opengl

import android.opengl.GLES32
import java.nio.IntBuffer


open class IndexedModel : ArrayModel() {
    protected var indexBuffer: IntBuffer? = null
    protected var indexCount = 0

    override fun drawFunc() {
        if (indexBuffer == null || indexCount == 0) {
            return
        }
        GLES32.glDrawElements(GLES32.GL_TRIANGLES, indexCount, GLES32.GL_UNSIGNED_INT, indexBuffer)
    }

    companion object {
        const val BYTES_PER_INT = 4
    }
}
