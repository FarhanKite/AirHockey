package com.raywenderlich.airhockey

import android.content.Context
import android.opengl.GLES20.GL_COLOR_BUFFER_BIT
import android.opengl.GLES20.glClear
import android.opengl.GLES20.glClearColor
import android.opengl.GLES20.glViewport
import android.opengl.GLSurfaceView
import android.opengl.Matrix.invertM
import android.opengl.Matrix.multiplyMM
import android.opengl.Matrix.multiplyMV
import android.opengl.Matrix.perspectiveM
import android.opengl.Matrix.rotateM
import android.opengl.Matrix.setIdentityM
import android.opengl.Matrix.setLookAtM
import android.opengl.Matrix.translateM
import android.util.Log
import com.raywenderlich.airhockey.objects.Mallet
import com.raywenderlich.airhockey.objects.Puck
import com.raywenderlich.airhockey.objects.Table
import com.raywenderlich.airhockey.programs.ColorShaderProgram
import com.raywenderlich.airhockey.programs.TextureShaderProgram
import com.raywenderlich.airhockey.util.Geometry
import com.raywenderlich.airhockey.util.Geometry.Point
import com.raywenderlich.airhockey.util.Geometry.Ray
import com.raywenderlich.airhockey.util.Geometry.Sphere
import com.raywenderlich.airhockey.util.TextureHelper
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import kotlin.math.max
import kotlin.math.min


class AirHockeyRenderer(private val context: Context) : GLSurfaceView.Renderer {

    private val projectionMatrix = FloatArray(16)
    private val modelMatrix = FloatArray(16)
    private val viewMatrix = FloatArray(16)
    private val viewProjectionMatrix = FloatArray(16)
    private val modelViewProjectionMatrix = FloatArray(16)
    private val invertedViewProjectionMatrix = FloatArray(16)

    private lateinit var table: Table
    private lateinit var mallet: Mallet
    private lateinit var puck: Puck
    private lateinit var textureProgram: TextureShaderProgram
    private lateinit var colorProgram: ColorShaderProgram
    private var texture: Int = 0

    private var malletPressed = false
    private lateinit var blueMalletPosition: Point
    private lateinit var previousBlueMalletPosition: Point

    private val leftBound = -0.5f
    private val rightBound = 0.5f
    private val farBound = -0.8f
    private val nearBound = 0.8f

    private lateinit var puckPosition: Point
    private lateinit var puckVector: Geometry.Vector

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        glClearColor(0f, 0f, 0f, 0f)

        table = Table()
        mallet = Mallet(0.08f, 0.15f, 32)
        puck = Puck(0.06f, 0.02f, 32)

        blueMalletPosition = Point(0f, mallet.height / 2f, 0.4f)
        previousBlueMalletPosition = blueMalletPosition  //

        puckPosition = Point(0f, puck.height / 2f, 0f)
        puckVector = Geometry.Vector(0f, 0f, 0f)

        textureProgram = TextureShaderProgram(context)
        colorProgram = ColorShaderProgram(context)

        texture = TextureHelper.loadTexture(context, R.drawable.air_hockey_surface)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        glViewport(0, 0, width, height)

        perspectiveM(
            projectionMatrix,
            0,
            45f,
            width.toFloat() * 1.4f / height.toFloat(),
            1f,
            10f
        )
        setLookAtM(viewMatrix, 0, 0f, 1.2f, 2.2f, 0f, 0f, 0f, 0f, 1f, 0f)
    }

    override fun onDrawFrame(gl: GL10?) {
        glClear(GL_COLOR_BUFFER_BIT)

        // Update puck position
        puckPosition = puckPosition.translate(puckVector)

        // Bounce puck off left/right walls
        if (puckPosition.x < leftBound + puck.radius
            || puckPosition.x > rightBound - puck.radius
        ) {
            puckVector = Geometry.Vector(-puckVector.x, puckVector.y, puckVector.z)
        }

        // Bounce puck off far/near walls
        if (puckPosition.z < farBound + puck.radius
            || puckPosition.z > nearBound - puck.radius
        ) {
            puckVector = Geometry.Vector(puckVector.x, puckVector.y, -puckVector.z)
        }

        // Clamp puck within bounds
        puckPosition = Point(
            clamp(puckPosition.x, leftBound + puck.radius, rightBound - puck.radius),
            puckPosition.y,
            clamp(puckPosition.z, farBound + puck.radius, nearBound - puck.radius)
        )

        // Build view-projection matrix and its inverse (needed for touch ray casting)
        multiplyMM(viewProjectionMatrix, 0, projectionMatrix, 0, viewMatrix, 0)
        invertM(invertedViewProjectionMatrix, 0, viewProjectionMatrix, 0)

        // Draw table
        positionTableInScene()
        textureProgram.useProgram()
        textureProgram.setUniforms(modelViewProjectionMatrix, texture)
        table.bindData(textureProgram)
        table.draw()

        // Draw red mallet (opponent - fixed position)
        positionObjectInScene(0f, mallet.height / 2f, -0.4f)
        colorProgram.useProgram()
        colorProgram.setUniforms(modelViewProjectionMatrix, 1f, 0f, 0f)
        mallet.bindData(colorProgram)
        mallet.draw()

        // Draw blue mallet (player - moves with touch)
        positionObjectInScene(blueMalletPosition.x, blueMalletPosition.y, blueMalletPosition.z)
        colorProgram.setUniforms(modelViewProjectionMatrix, 0f, 0f, 1f)
        mallet.draw()

        // Draw puck
        positionObjectInScene(puckPosition.x, puckPosition.y, puckPosition.z)
        colorProgram.setUniforms(modelViewProjectionMatrix, 0.8f, 0.8f, 1f)
        puck.bindData(colorProgram)
        puck.draw()

        // Apply friction to puck
        puckVector = puckVector.scale(0.99f)
    }

    private fun positionTableInScene() {
        setIdentityM(modelMatrix, 0)
        rotateM(modelMatrix, 0, -90f, 1f, 0f, 0f)
        multiplyMM(modelViewProjectionMatrix, 0, viewProjectionMatrix, 0, modelMatrix, 0)
    }

    private fun positionObjectInScene(x: Float, y: Float, z: Float) {
        setIdentityM(modelMatrix, 0)
        translateM(modelMatrix, 0, x, y, z)
        multiplyMM(modelViewProjectionMatrix, 0, viewProjectionMatrix, 0, modelMatrix, 0)
    }

    fun handleTouchPress(normalizedX: Float, normalizedY: Float) {
        Log.d("AirHockeyRenderer", "press...")

        val ray = convertNormalized2DPointToRay(normalizedX, normalizedY)

        val malletBoundingSphere = Sphere(
            Point(blueMalletPosition.x, blueMalletPosition.y, blueMalletPosition.z),
            mallet.height / 2f
        )

        malletPressed = Geometry.intersects(malletBoundingSphere, ray)
    }

    private fun convertNormalized2DPointToRay(normalizedX: Float, normalizedY: Float): Ray {
        val nearPointNdc = floatArrayOf(normalizedX, normalizedY, -1f, 1f)
        val farPointNdc = floatArrayOf(normalizedX, normalizedY, 1f, 1f)

        val nearPointWorld = FloatArray(4)
        val farPointWorld = FloatArray(4)

        multiplyMV(nearPointWorld, 0, invertedViewProjectionMatrix, 0, nearPointNdc, 0)
        multiplyMV(farPointWorld, 0, invertedViewProjectionMatrix, 0, farPointNdc, 0)

        divideByW(nearPointWorld)
        divideByW(farPointWorld)

        val nearPointRay = Point(nearPointWorld[0], nearPointWorld[1], nearPointWorld[2])
        val farPointRay = Point(farPointWorld[0], farPointWorld[1], farPointWorld[2])

        return Ray(nearPointRay, Geometry.vectorBetween(nearPointRay, farPointRay))
    }

    private fun divideByW(vector: FloatArray) {
        vector[0] /= vector[3]
        vector[1] /= vector[3]
        vector[2] /= vector[3]
    }

    fun handleTouchDrag(normalizedX: Float, normalizedY: Float) {
        Log.d("AirHockeyRenderer", "drag...")

        if (malletPressed) {
            val ray = convertNormalized2DPointToRay(normalizedX, normalizedY)
            val plane = Geometry.Plane(Point(0f, 0f, 0f), Geometry.Vector(0f, 1f, 0f))
            val touchedPoint = Geometry.intersectionPoint(ray, plane)

            previousBlueMalletPosition = blueMalletPosition

            // Update blue mallet position, clamped to the player's half of the table
            blueMalletPosition = Point(
                clamp(touchedPoint.x, leftBound + mallet.radius, rightBound - mallet.radius),
                mallet.height / 2f,
                clamp(touchedPoint.z, farBound + mallet.radius, nearBound - mallet.radius)
            )

            // Check if mallet struck the puck
            val distance = Geometry.vectorBetween(blueMalletPosition, puckPosition).length()
            if (distance < (puck.radius + mallet.radius)) {
                // Launch puck based on mallet's velocity vector
                puckVector = Geometry.vectorBetween(previousBlueMalletPosition, blueMalletPosition)
            }
        }
    }

    private fun clamp(value: Float, min: Float, max: Float): Float {
        return min(max, max(value, min))
    }
}