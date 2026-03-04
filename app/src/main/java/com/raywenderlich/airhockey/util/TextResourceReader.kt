package com.raywenderlich.airhockey.util

import android.content.Context
import android.content.res.Resources
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

object TextResourceReader {
    fun readTextFileFromResource(context: Context, resourceId: Int): String {
        return try {
            context.resources.openRawResource(resourceId).bufferedReader().use { it.readText() }
        } catch (e: IOException) {
            throw RuntimeException("Could not open resource: $resourceId", e)
        } catch (nfe: Resources.NotFoundException) {
            throw RuntimeException("Resource not found: $resourceId", nfe)
        }
    }
}