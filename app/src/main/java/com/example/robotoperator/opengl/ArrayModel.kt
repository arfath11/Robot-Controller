package com.example.robotoperator.opengl
import android.opengl.GLES32
import android.opengl.Matrix
import android.util.Log
import com.example.robotoperator.util.Util.compileProgram
import java.nio.FloatBuffer
import com.example.robotoperator.R
import com.example.robotoperator.domain.model.PointCloud
import kotlin.collections.get


open class ArrayModel : Model() {
    // Vertices, normals will be populated by subclasses
    var vertexCount = 0
        protected set

    override var vertexBuffer: FloatBuffer? = null
    protected var normalBuffer: FloatBuffer? = null
    var colorBuffer: FloatBuffer? = null
    protected var useColorBuffer = false

    // Add a buffer for annotated point indices
    protected var annotationIndexBuffer: FloatBuffer? = null
    protected var annotationColorBuffer: FloatBuffer? = null
    protected var annotationCount = 0

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

    // Optimized method to load annotations directly into GPU memory
    open fun loadAnnotatedPoints(annotatedPoints: List<PointCloud>) {
        Log.d("ArrayModel", "Loading annotated points: Count=${annotatedPoints.size}")
        
        // Skip if there's no vertex buffer to reference
        if (vertexBuffer == null) {
            Log.e("ArrayModel", "Cannot load annotations: vertex buffer is null")
            return
        }

        // Calculate total number of points across all point clouds
        val totalPoints = annotatedPoints.sumOf { it.points.size }
        Log.d("ArrayModel", "Total points to annotate: $totalPoints")
        
        if (totalPoints == 0) {
            Log.d("ArrayModel", "No points to annotate, skipping")
            return
        }
        
        // We'll use a spatial index approach for efficiency
        buildSpatialIndexAndColorPoints(annotatedPoints)
    }
    
    // More efficient implementation using a spatial index
    private fun buildSpatialIndexAndColorPoints(annotatedPoints: List<PointCloud>) {
        if (colorBuffer == null || vertexBuffer == null) return
        
        // Create a spatial hash map for faster lookup
        // We'll use a grid-based approach with cells of size 0.1 units
        val cellSize = 0.1f
        val spatialIndex = HashMap<String, MutableList<IndexedPoint>>()
        
        // Get vertices from vertex buffer for processing
        val vertCount = vertexBuffer!!.capacity() / 3
        val vertices = FloatArray(vertexBuffer!!.capacity())
        vertexBuffer!!.position(0)
        vertexBuffer!!.get(vertices)
        vertexBuffer!!.position(0)
        
        // Build the spatial index from the vertex buffer
        Log.d("ArrayModel", "Building spatial index for $vertCount vertices")
        for (i in 0 until vertCount) {
            val vIdx = i * 3
            if (vIdx + 2 >= vertices.size) continue
            
            val x = vertices[vIdx]
            val y = vertices[vIdx + 1]
            val z = vertices[vIdx + 2]
            
            // Calculate cell coordinates
            val cellX = (x / cellSize).toInt()
            val cellY = (y / cellSize).toInt()
            val cellZ = (z / cellSize).toInt()
            
            // Create cell key and add point
            val cellKey = "$cellX:$cellY:$cellZ"
            val point = IndexedPoint(i, x, y, z)
            
            if (!spatialIndex.containsKey(cellKey)) {
                spatialIndex[cellKey] = mutableListOf()
            }
            spatialIndex[cellKey]?.add(point)
        }
        
        Log.d("ArrayModel", "Spatial index built with ${spatialIndex.size} cells")
        
        // Get the color buffer for updating, but we'll only modify annotated points
        val colors = FloatArray(colorBuffer!!.capacity())
        colorBuffer!!.position(0)
        colorBuffer!!.get(colors)
        colorBuffer!!.position(0)
        
        var updated = 0
        
        // Process each point cloud
        for (pointCloud in annotatedPoints) {
            val color = pointCloud.annotationType.defaultColor
            Log.d("ArrayModel", "Processing ${pointCloud.points.size} points for type ${pointCloud.annotationType.name}")
            
            // Convert color to OpenGL format
            val red = ((color shr 16) and 0xFF) / 255f
            val green = ((color shr 8) and 0xFF) / 255f
            val blue = (color and 0xFF) / 255f
            val alpha = ((color shr 24) and 0xFF) / 255f
            
            // Process each point in this point cloud
            for (point in pointCloud.points) {
                val x = point[0]
                val y = point[1]
                val z = point[2]
                
                // Calculate cell for this point
                val cellX = (x / cellSize).toInt()
                val cellY = (y / cellSize).toInt()
                val cellZ = (z / cellSize).toInt()
                
                // Check this cell and adjacent cells for close points
                for (dx in -1..1) {
                    for (dy in -1..1) {
                        for (dz in -1..1) {
                            val neighborKey = "${cellX + dx}:${cellY + dy}:${cellZ + dz}"
                            val cellPoints = spatialIndex[neighborKey] ?: continue
                            
                            // Check each point in this cell
                            for (indexedPoint in cellPoints) {
                                // Calculate distance
                                val dist = squaredDistance(
                                    x, y, z,
                                    indexedPoint.x, indexedPoint.y, indexedPoint.z
                                )
                                
                                // If close enough, color this point
                                if (dist < 0.0001f) { // Small threshold for "same" point
                                    val cIdx = indexedPoint.index * 4
                                    if (cIdx + 3 < colors.size) {
                                        colors[cIdx] = red
                                        colors[cIdx + 1] = green
                                        colors[cIdx + 2] = blue
                                        colors[cIdx + 3] = alpha
                                        updated++
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        
        Log.d("ArrayModel", "Updated $updated points")
        
        // Update the color buffer
        colorBuffer!!.position(0)
        colorBuffer!!.put(colors)
        colorBuffer!!.position(0)
    }
    
    private fun squaredDistance(x1: Float, y1: Float, z1: Float, x2: Float, y2: Float, z2: Float): Float {
        val dx = x1 - x2
        val dy = y1 - y2
        val dz = z1 - z2
        return dx * dx + dy * dy + dz * dz
    }
    
    // Helper class for spatial indexing
    private data class IndexedPoint(
        val index: Int,
        val x: Float,
        val y: Float,
        val z: Float
    )
}
