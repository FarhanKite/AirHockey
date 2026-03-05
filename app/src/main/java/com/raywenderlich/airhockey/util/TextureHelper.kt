package com.raywenderlich.airhockey.util

import android.content.Context
import android.graphics.BitmapFactory
import android.opengl.GLES20.*
import android.opengl.GLUtils
import android.util.Log

object TextureHelper {

    private const val TAG = "TextureHelper"

    fun loadTexture(context: Context, resourceId: Int): Int {
        val textureObjectIds = IntArray(1)
        glGenTextures(1, textureObjectIds, 0)

        if (textureObjectIds[0] == 0) {
            if (LoggerConfig.ON) {
                Log.w(TAG, "Could not generate a new OpenGL texture object.")
            }
            return 0
        }

        val options = BitmapFactory.Options().apply { inScaled = false }

        val bitmap = BitmapFactory.decodeResource(context.resources, resourceId, options)

        if (bitmap == null) {
            if (LoggerConfig.ON) {
                Log.w(TAG, "Resource ID $resourceId could not be decoded.")
            }
            glDeleteTextures(1, textureObjectIds, 0)
            return 0
        }

        // Bind the texture
        glBindTexture(GL_TEXTURE_2D, textureObjectIds[0])

        // Set filtering
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR)

        // Load bitmap data into OpenGL
        GLUtils.texImage2D(GL_TEXTURE_2D, 0, bitmap, 0)
        bitmap.recycle()

        // Generate mipmaps
        glGenerateMipmap(GL_TEXTURE_2D)

        // Unbind texture
        glBindTexture(GL_TEXTURE_2D, 0)

        return textureObjectIds[0]
    }
}