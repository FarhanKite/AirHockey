package com.raywenderlich.airhockey.programs

import android.content.Context
import android.opengl.GLES20.glUseProgram
import com.raywenderlich.airhockey.util.ShaderHelper
import com.raywenderlich.airhockey.util.TextResourceReader

open class ShaderProgram(context: Context, vertexShaderResourceId: Int, fragmentShaderResourceId: Int) {

    // Uniform constants
    protected val U_MATRIX = "u_Matrix"
    protected val U_TEXTURE_UNIT = "u_TextureUnit"

    // Attribute constants
    protected val A_POSITION = "a_Position"
    protected val A_COLOR = "a_Color"
    protected val A_TEXTURE_COORDINATES = "a_TextureCoordinates"

    // Shader program
    protected val program: Int

    init {
        // Compile the shaders and link the program
        val vertexShaderSource = TextResourceReader.readTextFileFromResource(context, vertexShaderResourceId)
        val fragmentShaderSource = TextResourceReader.readTextFileFromResource(context, fragmentShaderResourceId)
        program = ShaderHelper.buildProgram(vertexShaderSource, fragmentShaderSource)
    }

    // Set the current OpenGL shader program to this program
    fun useProgram() {
        glUseProgram(program)
    }
}