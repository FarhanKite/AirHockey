package com.raywenderlich.airhockey.programs

import android.content.Context
import android.opengl.GLES20.glGetAttribLocation
import android.opengl.GLES20.glGetUniformLocation
import android.opengl.GLES20.glUniformMatrix4fv
import com.raywenderlich.airhockey.R

class ColorShaderProgram(context: Context) :
    ShaderProgram(context, R.raw.simple_vertex_shader, R.raw.simple_fragment_shader) {

    // Uniform locations
    private val uMatrixLocation: Int = glGetUniformLocation(program, U_MATRIX)

    // Attribute locations
    private val aPositionLocation: Int = glGetAttribLocation(program, A_POSITION)
    private val aColorLocation: Int = glGetAttribLocation(program, A_COLOR)

    // Pass the matrix into the shader program
    fun setUniforms(matrix: FloatArray) {
        glUniformMatrix4fv(uMatrixLocation, 1, false, matrix, 0)
    }

    // Getters for attribute locations
    val positionAttributeLocation: Int
        get() = aPositionLocation

    val colorAttributeLocation: Int
        get() = aColorLocation
}