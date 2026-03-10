//package com.raywenderlich.airhockey
//
//import android.opengl.GLSurfaceView
//import android.os.Bundle
//import android.view.MotionEvent
//import android.view.View
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.activity.enableEdgeToEdge
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.foundation.layout.padding
//import androidx.compose.material3.Scaffold
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.tooling.preview.Preview
//import androidx.compose.ui.viewinterop.AndroidView
//import com.raywenderlich.airhockey.AirHockeyRenderer
//import com.raywenderlich.airhockey.ui.theme.AirHockeyTheme
//
//class MainActivity : ComponentActivity() {
//
//    private lateinit var glSurfaceView: GLSurfaceView
//    private lateinit var airHockeyRenderer: AirHockeyRenderer
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        glSurfaceView.setOnTouchListener(new OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                if (event != null) {
//// Convert touch coordinates into normalized device
//// coordinates, keeping in mind that Android's Y
//// coordinates are inverted.
//                    final float normalizedX =
//                        (event.getX() / (float) v.getWidth()) * 2 - 1;
//                    final float normalizedY =
//                        -((event.getY() / (float) v.getHeight()) * 2 - 1);
//
//
//        setContent {
//            AirHockeyTheme {
//                AndroidView(
//                    modifier = Modifier.fillMaxSize(),
//                    factory = { context ->
//                        glSurfaceView = GLSurfaceView(context).apply {
//                            setEGLContextClientVersion(2)
//                            airHockeyRenderer = AirHockeyRenderer(context)
//                            // setRenderer(AirHockeyRenderer(context))
//                            setRenderer(airHockeyRenderer)
//                            renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
//                        }
//                        glSurfaceView
//                    }
//                )
//            }
//        }
//    }
//
//    override fun onPause() {
//        super.onPause()
//        if (::glSurfaceView.isInitialized) {
//            glSurfaceView.onPause()
//        }
//    }
//
//    override fun onResume() {
//        super.onResume()
//        if (::glSurfaceView.isInitialized) {
//            glSurfaceView.onResume()
//        }
//    }
//}


//
//
//
//package com.raywenderlich.airhockey
//
//import android.opengl.GLSurfaceView
//import android.os.Bundle
//import android.view.MotionEvent
//import androidx.activity.ComponentActivity
//import androidx.activity.compose.setContent
//import androidx.compose.foundation.layout.fillMaxSize
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.viewinterop.AndroidView
//import com.raywenderlich.airhockey.ui.theme.AirHockeyTheme
//
//class MainActivity : ComponentActivity() {
//
//    private lateinit var glSurfaceView: GLSurfaceView
//    private lateinit var airHockeyRenderer: AirHockeyRenderer
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        setContent {
//            AirHockeyTheme {
//                AndroidView(
//                    modifier = Modifier.fillMaxSize(),
//                    factory = { context ->
//
//                        glSurfaceView = GLSurfaceView(context).apply {
//                            setEGLContextClientVersion(2)
//
//                            airHockeyRenderer = AirHockeyRenderer(context)
//                            setRenderer(airHockeyRenderer)
//
//                            renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
//                        }
//
//                        glSurfaceView
//                    }
//                )
//            }
//        }
//    }
//
//    override fun onPause() {
//        super.onPause()
//        if (::glSurfaceView.isInitialized) {
//            glSurfaceView.onPause()
//        }
//    }
//
//    override fun onResume() {
//        super.onResume()
//        if (::glSurfaceView.isInitialized) {
//            glSurfaceView.onResume()
//        }
//    }
//}





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

                            // Kotlin-style touch listener
                            setOnTouchListener { v, event ->

                                if (event != null) {
                                    // Convert touch coordinates to normalized device coordinates
                                    val normalizedX = (event.x / v.width) * 2f - 1f
                                    val normalizedY = -((event.y / v.height) * 2f - 1f)

                                    when (event.action) {
                                        MotionEvent.ACTION_DOWN -> {
                                            glSurfaceView.queueEvent {
                                                airHockeyRenderer.handleTouchPress(
                                                    normalizedX, normalizedY
                                                )
                                            }
                                        }
                                        MotionEvent.ACTION_MOVE -> {
                                            glSurfaceView.queueEvent {
                                                airHockeyRenderer.handleTouchDrag(
                                                    normalizedX, normalizedY
                                                )
                                            }
                                        }
                                    }
                                }

                                true // Event handled
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