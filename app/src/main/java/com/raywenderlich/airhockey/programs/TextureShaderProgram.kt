package com.raywenderlich.airhockey.programs

import android.content.Context
import android.opengl.GLES20.*
import com.raywenderlich.airhockey.R

class TextureShaderProgram(context: Context) :
    ShaderProgram(context, R.raw.texture_vertex_shader, R.raw.texture_fragment_shader) {

    // Uniform locations
    private val uMatrixLocation: Int = glGetUniformLocation(program, U_MATRIX)
    private val uTextureUnitLocation: Int = glGetUniformLocation(program, U_TEXTURE_UNIT)

    // Attribute locations
    private val aPositionLocation: Int = glGetAttribLocation(program, A_POSITION)
    private val aTextureCoordinatesLocation: Int = glGetAttribLocation(program, A_TEXTURE_COORDINATES)

    // Set the matrix and texture ID for this shader program
    fun setUniforms(matrix: FloatArray, textureId: Int) {
        // Pass the matrix into the shader program
        glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0)

        // Set the active texture unit to texture unit 0
        glActiveTexture(GL_TEXTURE0)

        // Bind the texture to this unit
        glBindTexture(GL_TEXTURE_2D, textureId)

        // Tell the texture uniform sampler to use texture unit 0
        glUniform1i(uTextureUnitLocation, 0)
    }

    // Getters for attribute locations
    val positionAttributeLocation: Int
        get() = aPositionLocation

    val textureCoordinatesAttributeLocation: Int
        get() = aTextureCoordinatesLocation
}