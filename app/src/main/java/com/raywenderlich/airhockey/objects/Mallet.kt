package com.raywenderlich.airhockey.objects

import android.opengl.GLES20.GL_POINTS
import android.opengl.GLES20.glDrawArrays
import com.raywenderlich.airhockey.Constants.BYTES_PER_FLOAT
import com.raywenderlich.airhockey.data.VertexArray
import com.raywenderlich.airhockey.programs.ColorShaderProgram

class Mallet {

    companion object {
        private const val POSITION_COMPONENT_COUNT = 2
        private const val COLOR_COMPONENT_COUNT = 3
        private val STRIDE = (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT) * BYTES_PER_FLOAT

        // Vertex data: X, Y, R, G, B
        private val VERTEX_DATA = floatArrayOf(
            0f, -0.4f, 0f, 0f, 1f,
            0f, 0.4f, 1f, 0f, 0f
        )
    }

    private val vertexArray: VertexArray = VertexArray(VERTEX_DATA)

    // Bind the vertex data to the shader program attributes
    fun bindData(colorProgram: ColorShaderProgram) {
        vertexArray.setVertexAttribPointer(
            0,
            colorProgram.positionAttributeLocation,
            POSITION_COMPONENT_COUNT,
            STRIDE
        )
        vertexArray.setVertexAttribPointer(
            POSITION_COMPONENT_COUNT,
            colorProgram.colorAttributeLocation,
            COLOR_COMPONENT_COUNT,
            STRIDE
        )
    }

    fun draw() {
        glDrawArrays(GL_POINTS, 0, 2)
    }
}