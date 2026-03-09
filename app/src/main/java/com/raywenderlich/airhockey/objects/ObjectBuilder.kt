package com.raywenderlich.airhockey.objects

import android.opengl.GLES20.*
import com.raywenderlich.airhockey.util.Geometry.Circle
import com.raywenderlich.airhockey.util.Geometry.Cylinder
import com.raywenderlich.airhockey.util.Geometry.Point
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.PI

class ObjectBuilder private constructor(sizeInVertices: Int) {

    fun interface DrawCommand {
        fun draw()
    }

    class GeneratedData(
        val vertexData: FloatArray,
        val drawList: List<DrawCommand>
    )

    private val drawList = mutableListOf<DrawCommand>()

    private val vertexData = FloatArray(sizeInVertices * FLOATS_PER_VERTEX)

    private var offset = 0

    private fun build(): GeneratedData {
        return GeneratedData(vertexData, drawList)
    }

    private fun appendCircle(circle: Circle, numPoints: Int) {

        val startVertex = offset / FLOATS_PER_VERTEX
        val numVertices = sizeOfCircleInVertices(numPoints)

        // center
        vertexData[offset++] = circle.center.x
        vertexData[offset++] = circle.center.y
        vertexData[offset++] = circle.center.z

        for (i in 0..numPoints) {

            val angleInRadians = (i.toFloat() / numPoints.toFloat()) *
                    (PI.toFloat() * 2f)

            vertexData[offset++] =
                circle.center.x + circle.radius * cos(angleInRadians)

            vertexData[offset++] = circle.center.y

            vertexData[offset++] =
                circle.center.z + circle.radius * sin(angleInRadians)
        }

        drawList.add(
            DrawCommand {
                glDrawArrays(GL_TRIANGLE_FAN, startVertex, numVertices)
            }
        )
    }

    private fun appendOpenCylinder(cylinder: Cylinder, numPoints: Int) {

        val startVertex = offset / FLOATS_PER_VERTEX
        val numVertices = sizeOfOpenCylinderInVertices(numPoints)

        val yStart = cylinder.center.y - (cylinder.height / 2f)
        val yEnd = cylinder.center.y + (cylinder.height / 2f)

        for (i in 0..numPoints) {

            val angleInRadians =
                (i.toFloat() / numPoints.toFloat()) * (PI.toFloat() * 2f)

            val xPosition =
                cylinder.center.x + cylinder.radius * cos(angleInRadians)

            val zPosition =
                cylinder.center.z + cylinder.radius * sin(angleInRadians)

            vertexData[offset++] = xPosition
            vertexData[offset++] = yStart
            vertexData[offset++] = zPosition

            vertexData[offset++] = xPosition
            vertexData[offset++] = yEnd
            vertexData[offset++] = zPosition
        }

        drawList.add(
            DrawCommand {
                glDrawArrays(GL_TRIANGLE_STRIP, startVertex, numVertices)
            }
        )
    }

    companion object {

        private const val FLOATS_PER_VERTEX = 3

        private fun sizeOfCircleInVertices(numPoints: Int): Int {
            return 1 + (numPoints + 1)
        }

        private fun sizeOfOpenCylinderInVertices(numPoints: Int): Int {
            return (numPoints + 1) * 2
        }

        fun createPuck(puck: Cylinder, numPoints: Int): GeneratedData {

            val size = sizeOfCircleInVertices(numPoints) +
                    sizeOfOpenCylinderInVertices(numPoints)

            val builder = ObjectBuilder(size)

            val puckTop = Circle(
                puck.center.translateY(puck.height / 2f),
                puck.radius
            )

            builder.appendCircle(puckTop, numPoints)
            builder.appendOpenCylinder(puck, numPoints)

            return builder.build()
        }

        fun createMallet(
            center: Point,
            radius: Float,
            height: Float,
            numPoints: Int
        ): GeneratedData {

            val size =
                sizeOfCircleInVertices(numPoints) * 2 +
                        sizeOfOpenCylinderInVertices(numPoints) * 2

            val builder = ObjectBuilder(size)

            // Base
            val baseHeight = height * 0.25f

            val baseCircle = Circle(
                center.translateY(-baseHeight),
                radius
            )

            val baseCylinder = Cylinder(
                baseCircle.center.translateY(-baseHeight / 2f),
                radius,
                baseHeight
            )

            builder.appendCircle(baseCircle, numPoints)
            builder.appendOpenCylinder(baseCylinder, numPoints)

            // Handle
            val handleHeight = height * 0.75f
            val handleRadius = radius / 3f

            val handleCircle = Circle(
                center.translateY(height * 0.5f),
                handleRadius
            )

            val handleCylinder = Cylinder(
                handleCircle.center.translateY(-handleHeight / 2f),
                handleRadius,
                handleHeight
            )

            builder.appendCircle(handleCircle, numPoints)
            builder.appendOpenCylinder(handleCylinder, numPoints)

            return builder.build()
        }
    }
}