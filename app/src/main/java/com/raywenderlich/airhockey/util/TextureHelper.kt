package com.raywenderlich.airhockey.util

import android.R.attr.bitmap
import android.content.Context
import android.graphics.BitmapFactory
import android.opengl.GLES10.GL_TEXTURE_2D
import android.opengl.GLES10.glBindTexture
import android.opengl.GLES10.glDeleteTextures
import android.opengl.GLES10.glGenTextures
import android.util.Log


class TextureHelper {
    private val TAG = "TextureHelper"

    fun loadTexture(context: Context, resourceId: Int): Int {
        val textureObjectIds = IntArray(1)
        glGenTextures(1, textureObjectIds, 0)

        if (textureObjectIds[0] == 0) {
            if (LoggerConfig.ON) {
                Log.w(TAG, "Could not generate a new OpenGL texture object.")
            }
            return 0
        }

        val options: BitmapFactory.Options = BitmapFactory.Options()
        options.inScaled = false

        val bitmap = BitmapFactory.decodeResource(
            context.resources,
            resourceId,
            options
        )

        if (bitmap == null) {
            if (LoggerConfig.ON) {
                Log.w(TAG, "Resource ID $resourceId could not be decoded.")
            }
            glDeleteTextures(1, textureObjectIds, 0)
            return 0
        }

        glBindTexture(GL_TEXTURE_2D, textureObjectIds[0]);

        return -111111
    }
}