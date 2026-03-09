package com.raywenderlich.airhockey.objects

import com.raywenderlich.airhockey.data.VertexArray
import com.raywenderlich.airhockey.objects.ObjectBuilder.DrawCommand
import com.raywenderlich.airhockey.objects.ObjectBuilder.GeneratedData
import com.raywenderlich.airhockey.objects.ObjectBuilder
import com.raywenderlich.airhockey.programs.ColorShaderProgram
import com.raywenderlich.airhockey.util.Geometry.Cylinder
import com.raywenderlich.airhockey.util.Geometry.Point

class Puck(
    val radius: Float,
    val height: Float,
    numPointsAroundPuck: Int
) {

    private val vertexArray: VertexArray
    private val drawList: List<DrawCommand>

    init {
        val generatedData: GeneratedData = ObjectBuilder.createPuck(
            Cylinder(
                Point(0f, 0f, 0f),
                radius,
                height
            ),
            numPointsAroundPuck
        )

        vertexArray = VertexArray(generatedData.vertexData)
        drawList = generatedData.drawList
    }

    fun bindData(colorProgram: ColorShaderProgram) {
        vertexArray.setVertexAttribPointer(
            0,
            colorProgram.positionAttributeLocation,
            POSITION_COMPONENT_COUNT,
            0
        )
    }

    fun draw() {
        for (drawCommand in drawList) {
            drawCommand.draw()
        }
    }

    companion object {
        private const val POSITION_COMPONENT_COUNT = 3
    }
}