package com.raywenderlich.airhockey.objects

import android.opengl.GLES20.GL_TRIANGLE_FAN
import android.opengl.GLES20.glDrawArrays
import com.raywenderlich.airhockey.Constants.BYTES_PER_FLOAT
import com.raywenderlich.airhockey.data.VertexArray
import com.raywenderlich.airhockey.programs.TextureShaderProgram
import com.raywenderlich.airhockey.util.TextureHelper

class Table {

    companion object {
        const val POSITION_COMPONENT_COUNT = 2
        const val TEXTURE_COORDINATES_COMPONENT_COUNT = 2
        val STRIDE = (POSITION_COMPONENT_COUNT + TEXTURE_COORDINATES_COMPONENT_COUNT) * BYTES_PER_FLOAT

        // Vertex data (Triangle Fan)
        val VERTEX_DATA = floatArrayOf(
            // Order of coordinates: X, Y, S, T
            0f, 0f, 0.5f, 0.5f,
            -0.5f, -0.8f, 0f, 0.9f,
            0.5f, -0.8f, 1f, 0.9f,
            0.5f, 0.8f, 1f, 0.1f,
            -0.5f, 0.8f, 0f, 0.1f,
            -0.5f, -0.8f, 0f, 0.9f
        )
    }

    private val vertexArray: VertexArray = VertexArray(VERTEX_DATA)

    // Bind the vertex data to the shader program attributes
    fun bindData(textureProgram: TextureShaderProgram) {
        vertexArray.setVertexAttribPointer(
            0,
            textureProgram.positionAttributeLocation,
            POSITION_COMPONENT_COUNT,
            STRIDE
        )
        vertexArray.setVertexAttribPointer(
            POSITION_COMPONENT_COUNT,
            textureProgram.textureCoordinatesAttributeLocation,
            TEXTURE_COORDINATES_COMPONENT_COUNT,
            STRIDE
        )
    }

    fun draw() {
        glDrawArrays(GL_TRIANGLE_FAN, 0, 6)
    }
}