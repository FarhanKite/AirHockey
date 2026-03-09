package com.raywenderlich.airhockey

import android.content.Context
import android.opengl.GLES20.GL_COLOR_BUFFER_BIT
import android.opengl.GLES20.glClear
import android.opengl.GLES20.glClearColor
import android.opengl.GLES20.glViewport
import android.opengl.GLSurfaceView
import android.opengl.Matrix.multiplyMM
import android.opengl.Matrix.orthoM
import android.opengl.Matrix.perspectiveM
import android.opengl.Matrix.rotateM
import android.opengl.Matrix.setIdentityM
import android.opengl.Matrix.setLookAtM
import android.opengl.Matrix.translateM
import com.raywenderlich.airhockey.objects.Mallet
import com.raywenderlich.airhockey.objects.Puck
import com.raywenderlich.airhockey.objects.Table
import com.raywenderlich.airhockey.programs.ColorShaderProgram
import com.raywenderlich.airhockey.programs.TextureShaderProgram
import com.raywenderlich.airhockey.util.TextureHelper
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class AirHockeyRenderer(private val context: Context) : GLSurfaceView.Renderer {

    private val projectionMatrix = FloatArray(16)
    private val modelMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private val viewProjectionMatrix = FloatArray(16)
    private val modelViewProjectionMatrix = FloatArray(16)

    private lateinit var table: Table
    private lateinit var mallet: Mallet
    private lateinit var puck: Puck
    private lateinit var textureProgram: TextureShaderProgram
    private lateinit var colorProgram: ColorShaderProgram
    private var texture: Int = 0

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        // Set the background clear color
        glClearColor(0f, 0f, 0f, 0f)

        // Initialize objects
        table = Table()
        mallet = Mallet(0.08f, 0.15f, 32)
        puck = Puck(0.06f, 0.02f, 32)

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

        perspectiveM(
            projectionMatrix,
            0,                  // offset into the array
            45f,                // field of view in degrees
            width.toFloat() * 1.4f / height.toFloat(),  // aspect ratio
            1f,                 // near plane
            10f                 // far plane
        )
        setLookAtM(viewMatrix, 0, 0f, 1.2f, 2.2f, 0f, 0f, 0f, 0f, 1f, 0f)

//        perspectiveM(
//            projectionMatrix,
//            0,                  // offset into the array
//            45f,                // field of view in degrees
//            width.toFloat() / height.toFloat(),  // aspect ratio
//            1f,                 // near plane
//            10f                 // far plane
//        )
//        setLookAtM(viewMatrix, 0, 0f, 1.2f, 3f, 0f, 0f, 0f, 0f, 1f, 0f)

        // have to check working or not ...
//        translateM(modelMatrix, 0, 0f, 0f, -2.5f);
//        rotateM(modelMatrix, 0, -60f, 1f, 0f, 0f);
        // ...

//        if (width > height) {
//            // Landscape
//            orthoM(
//                projectionMatrix, 0,
//                -aspectRatio, aspectRatio,
//                -1f, 1f,
//                -1f, 1f
//            )
//        } else {
//            // Portrait or square
//            orthoM(
//                projectionMatrix, 0,
//                -1f, 1f,
//                -aspectRatio, aspectRatio,
//                -1f, 1f
//            )
//        }
    }

    override fun onDrawFrame(gl: GL10?) {
        // Clear the rendering surface
        glClear(GL_COLOR_BUFFER_BIT)

        multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0);

        positionTableInScene();
        textureProgram.useProgram();
        textureProgram.setUniforms(modelViewProjectionMatrix, texture);
        table.bindData(textureProgram);
        table.draw();

        // Draw the mallets.
        positionObjectInScene(0f, mallet.height / 2f, -0.4f);
        colorProgram.useProgram();
        colorProgram.setUniforms(modelViewProjectionMatrix, 1f, 0f, 0f);
        mallet.bindData(colorProgram);
        mallet.draw();
        positionObjectInScene(0f, mallet.height / 2f, 0.4f);
        colorProgram.setUniforms(modelViewProjectionMatrix, 0f, 0f, 1f);
        // Note that we don't have to define the object data twice -- we just
        // draw the same mallet again but in a different position and with a
        // different color.
        mallet.draw();

        // Draw the puck.
        positionObjectInScene(0f, puck.height / 2f, 0f);
        colorProgram.setUniforms(modelViewProjectionMatrix, 0.8f, 0.8f, 1f);
        puck.bindData(colorProgram);
        puck.draw();
    }

    private fun positionTableInScene() {
// The table is defined in terms of X & Y coordinates, so we rotate it
// 90 degrees to lie flat on the XZ plane.
        setIdentityM(modelMatrix, 0)
        rotateM(modelMatrix, 0, -90f, 1f, 0f, 0f)
        multiplyMM(
            modelViewProjectionMatrix, 0, viewProjectionMatrix,
            0, modelMatrix, 0
        )
    }

    private fun positionObjectInScene(x: Float, y: Float, z: Float) {
        setIdentityM(modelMatrix, 0)
        translateM(modelMatrix, 0, x, y, z)
        multiplyMM(
            modelViewProjectionMatrix, 0, viewProjectionMatrix,
            0, modelMatrix, 0
        )
    }
}