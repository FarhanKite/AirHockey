package com.raywenderlich.airhockey

import android.content.Context
import android.opengl.GLES20.*
import android.opengl.GLSurfaceView
import android.opengl.Matrix.orthoM
import com.raywenderlich.airhockey.objects.Mallet
import com.raywenderlich.airhockey.objects.Table
import com.raywenderlich.airhockey.programs.ColorShaderProgram
import com.raywenderlich.airhockey.programs.TextureShaderProgram
import com.raywenderlich.airhockey.util.TextureHelper
import com.raywenderlich.airhockey.R
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class AirHockeyRenderer(private val context: Context) : GLSurfaceView.Renderer {

    private val projectionMatrix = FloatArray(16)
    private val modelMatrix = FloatArray(16)

    private lateinit var table: Table
    private lateinit var mallet: Mallet
    private lateinit var textureProgram: TextureShaderProgram
    private lateinit var colorProgram: ColorShaderProgram
    private var texture: Int = 0

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        // Set the background clear color
        glClearColor(0f, 0f, 0f, 0f)

        // Initialize objects
        table = Table()
        mallet = Mallet()

        // Initialize shader programs
        textureProgram = TextureShaderProgram(context)
        colorProgram = ColorShaderProgram(context)

        // Load texture
        texture = TextureHelper.loadTexture(context, R.drawable.air_hockey_surface)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        glViewport(0, 0, width, height)

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
        // Clear the rendering surface
        glClear(GL_COLOR_BUFFER_BIT)

        // Draw the table
        textureProgram.useProgram()
        textureProgram.setUniforms(projectionMatrix, texture)
        table.bindData(textureProgram)
        table.draw()

        // Draw the mallets
        colorProgram.useProgram()
        colorProgram.setUniforms(projectionMatrix)
        mallet.bindData(colorProgram)
        mallet.draw()
    }
}