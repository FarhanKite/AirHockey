package com.raywenderlich.airhockey

import android.opengl.GLSurfaceView
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import com.raywenderlich.airhockey.AirHockeyRenderer
import com.raywenderlich.airhockey.ui.theme.AirHockeyTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AirHockeyTheme {
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = { context ->
                        GLSurfaceView(context).apply {
                            setEGLContextClientVersion(2)
                            setRenderer(AirHockeyRenderer(context))
                            renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
                        }
                    }
                )
            }
        }
    }
}