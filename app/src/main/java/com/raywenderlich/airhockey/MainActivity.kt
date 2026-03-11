package com.raywenderlich.airhockey

import android.opengl.GLSurfaceView
import android.os.Bundle
import android.view.MotionEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.raywenderlich.airhockey.ui.theme.AirHockeyTheme

class MainActivity : ComponentActivity() {

    private lateinit var glSurfaceView: GLSurfaceView
    private lateinit var airHockeyRenderer: AirHockeyRenderer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AirHockeyTheme {
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = { context ->

                        glSurfaceView = GLSurfaceView(context).apply {
                            setEGLContextClientVersion(2)

                            airHockeyRenderer = AirHockeyRenderer(context)
                            setRenderer(airHockeyRenderer)
                            renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY

                            setOnTouchListener { v, event ->

                                when (event.actionMasked) {

                                    MotionEvent.ACTION_DOWN,
                                    MotionEvent.ACTION_POINTER_DOWN -> {

                                        val pointerIndex = event.actionIndex
                                        val pointerId    = event.getPointerId(pointerIndex)
                                        val screenY      = event.getY(pointerIndex)

                                        val normalizedX = (event.getX(pointerIndex) / v.width) * 2f - 1f
                                        val normalizedY = -((event.getY(pointerIndex) / v.height) * 2f - 1f)

                                        v.performClick()

                                        glSurfaceView.queueEvent {
                                            if (screenY < v.height / 2f) {
                                                // Top half, red mallet
                                                airHockeyRenderer.assignPointer(pointerId, isBlueMallet = false)
                                                airHockeyRenderer.handleTouchPressRed(normalizedX, normalizedY)
                                            } else {
                                                // Bottom half, blue mallet
                                                airHockeyRenderer.assignPointer(pointerId, isBlueMallet = true)
                                                airHockeyRenderer.handleTouchPress(normalizedX, normalizedY)
                                            }
                                        }
                                    }

                                    MotionEvent.ACTION_MOVE -> {
                                        for (i in 0 until event.pointerCount) {
                                            val pointerId   = event.getPointerId(i)
                                            val normalizedX = (event.getX(i) / v.width) * 2f - 1f
                                            val normalizedY = -((event.getY(i) / v.height) * 2f - 1f)

                                            glSurfaceView.queueEvent {
                                                airHockeyRenderer.handleTouchDragById(normalizedX, normalizedY, pointerId)
                                            }
                                        }
                                    }

                                    MotionEvent.ACTION_UP,
                                    MotionEvent.ACTION_POINTER_UP -> {
                                        val pointerId = event.getPointerId(event.actionIndex)
                                        glSurfaceView.queueEvent {
                                            airHockeyRenderer.handleTouchRelease(pointerId)
                                        }
                                    }
                                }

                                true
                            }
                        }

                        glSurfaceView
                    }
                )
            }
        }
    }

    override fun onPause() {
        super.onPause()
        if (::glSurfaceView.isInitialized) {
            glSurfaceView.onPause()
        }
    }

    override fun onResume() {
        super.onResume()
        if (::glSurfaceView.isInitialized) {
            glSurfaceView.onResume()
        }
    }
}