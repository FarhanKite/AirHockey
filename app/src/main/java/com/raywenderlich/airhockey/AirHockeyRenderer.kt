package com.raywenderlich.airhockey

import android.content.Context
import android.opengl.GLES20
import android.opengl.GLES20.GL_FLOAT
import android.opengl.GLES20.GL_LINES
import android.opengl.GLES20.GL_POINTS
import android.opengl.GLES20.GL_TRIANGLE_FAN
import android.opengl.GLES20.glDrawArrays
import android.opengl.GLES20.glEnableVertexAttribArray
import android.opengl.GLES20.glGetAttribLocation
import android.opengl.GLES20.glGetUniformLocation
import android.opengl.GLES20.glUniformMatrix4fv
import android.opengl.GLES20.glUseProgram
import android.opengl.GLES20.glVertexAttribPointer
import android.opengl.GLSurfaceView
import android.opengl.Matrix.orthoM
import com.raywenderlich.airhockey.util.LoggerConfig
import com.raywenderlich.airhockey.util.ShaderHelper
import com.raywenderlich.airhockey.util.TextResourceReader
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10


const val POSITION_COMPONENT_COUNT = 2
const val BYTES_PER_FLOAT = 4

class AirHockeyRenderer(val context: Context) : GLSurfaceView.Renderer {

    val A_COLOR: String = "a_Color"
    private var aColorLocation = 0
    val COLOR_COMPONENT_COUNT: Int = 3
    val STRIDE: Int = (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT) * BYTES_PER_FLOAT

    private var program = 0

    val A_POSITION: String = "a_Position"
    var aPositionLocation = 0

    val U_MATRIX: String = "u_Matrix"
    private val projectionMatrix = FloatArray(16)
    private var uMatrixLocation = 0

    private val tableVerticesWithTriangles = floatArrayOf(
        // Order of coordinates: X, Y, R, G, B

        // Triangle Fan
        0f, 0f, 1f, 1f, 1f,
        -0.5f, -0.8f, 0.7f, 0.7f, 0.7f,
        0.5f, -0.8f, 0.7f, 0.7f, 0.7f,
        0.5f, 0.8f, 0.7f, 0.7f, 0.7f,
        -0.5f, 0.8f, 0.7f, 0.7f, 0.7f,
        -0.5f, -0.8f, 0.7f, 0.7f, 0.7f,

        // Line 1
        -0.5f, 0f, 1f, 0f, 0f,
        0.5f, 0f, 1f, 0f, 0f,

        // Mallets
        0f, -0.4f, 0f, 0f, 1f,
        0f, 0.4f, 1f, 0f, 0f
    )

    private val vertexData: FloatBuffer

    init {
        vertexData = ByteBuffer
            .allocateDirect(tableVerticesWithTriangles.size * BYTES_PER_FLOAT)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
        vertexData.put(tableVerticesWithTriangles)
        vertexData.position(0) // Reset buffer position after putting data
    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        // Set the clear color (background color)
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        val vertexShaderSource: String = TextResourceReader
            .readTextFileFromResource(context, R.raw.simple_vertex_shader)
        val fragmentShaderSource: String = TextResourceReader
            .readTextFileFromResource(context, R.raw.simple_fragment_shader)

        val vertexShader: Int = ShaderHelper.compileVertexShader(vertexShaderSource)
        val fragmentShader: Int = ShaderHelper.compileFragmentShader(fragmentShaderSource)

        program = ShaderHelper.linkProgram(vertexShader, fragmentShader);

        if (LoggerConfig.ON) {
            ShaderHelper.validateProgram(program);
        }

        glUseProgram(program);

//        uColorLocation = glGetUniformLocation(program, U_COLOR);
        uMatrixLocation = glGetUniformLocation(program, U_MATRIX);
        aColorLocation = glGetAttribLocation(program, A_COLOR);
        aPositionLocation = glGetAttribLocation(program, A_POSITION);

        vertexData.position(0);
        glVertexAttribPointer(aPositionLocation, POSITION_COMPONENT_COUNT, GL_FLOAT,
            false, STRIDE, vertexData);

        glEnableVertexAttribArray(aPositionLocation);

        vertexData.position(POSITION_COMPONENT_COUNT);
        glVertexAttribPointer(aColorLocation, COLOR_COMPONENT_COUNT, GL_FLOAT,
            false, STRIDE, vertexData);
        glEnableVertexAttribArray(aColorLocation);
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)

        val aspectRatio = if (width > height) {
            width.toFloat() / height.toFloat()
        } else {
            height.toFloat() / width.toFloat()
        }

        if (width > height) {
            // Landscape
            orthoM(
                projectionMatrix, 0,
                -aspectRatio, aspectRatio,
                -1f, 1f,
                -1f, 1f
            )
        } else {
            // Portrait or square
            orthoM(
                projectionMatrix, 0,
                -1f, 1f,
                -aspectRatio, aspectRatio,
                -1f, 1f
            )
        }
    }

    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

        glUniformMatrix4fv(uMatrixLocation, 1, false, projectionMatrix, 0);

        // TODO: Draw your table using vertexData and shaders
//        glUniform4f(uColorLocation, 1.0f, 1.0f, 1.0f, 1.0f);
        glDrawArrays(GL_TRIANGLE_FAN, 0, 6);

//        glUniform4f(uColorLocation, 1.0f, 0.0f, 0.0f, 1.0f);
        glDrawArrays(GL_LINES, 6, 2);

        // Draw the first mallet blue.
//        glUniform4f(uColorLocation, 0.0f, 0.0f, 1.0f, 1.0f);
        glDrawArrays(GL_POINTS, 8, 1);

        // Draw the second mallet red.
//        glUniform4f(uColorLocation, 1.0f, 0.0f, 0.0f, 1.0f);
        glDrawArrays(GL_POINTS, 9, 1);
    }
}