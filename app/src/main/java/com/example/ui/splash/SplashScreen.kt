package com.example.ui.splash

import android.net.Uri
import android.view.Gravity
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.VideoView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.viewinterop.AndroidView
import com.example.R
import com.example.ui.theme.SplashBackground
import java.util.concurrent.atomic.AtomicBoolean
import kotlinx.coroutines.delay
import kotlin.math.max

/**
 * Full-screen splash that plays res/raw/splash_intro.mp4 (silent), scaled to fill the screen
 * (centre-cropped). [onFinished] is called exactly once: when playback completes, if the
 * video fails, or after a safety timeout so the app can never get stuck on the splash.
 */
@Composable
fun SplashScreen(
    onFinished: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentOnFinished by rememberUpdatedState(onFinished)
    val finished = remember { AtomicBoolean(false) }
    val finish: () -> Unit = remember {
        { if (finished.compareAndSet(false, true)) currentOnFinished() }
    }

    LaunchedEffect(Unit) {
        delay(SAFETY_TIMEOUT_MS)
        finish()
    }

    Box(modifier = modifier.fillMaxSize().background(SplashBackground)) {
        AndroidView(
            modifier = Modifier.fillMaxSize(),
            factory = { context ->
                val container = FrameLayout(context).apply {
                    setBackgroundColor(SplashBackground.toArgb())
                    layoutParams = ViewGroup.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                    )
                }
                val video = VideoView(context)
                container.addView(
                    video,
                    FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        Gravity.CENTER
                    )
                )
                video.setVideoURI(
                    Uri.parse("android.resource://${context.packageName}/${R.raw.splash_intro}")
                )
                video.setOnPreparedListener { player ->
                    player.isLooping = false
                    player.setVolume(0f, 0f)
                    val vw = player.videoWidth.toFloat()
                    val vh = player.videoHeight.toFloat()
                    val cw = container.width.toFloat()
                    val ch = container.height.toFloat()
                    if (vw > 0f && vh > 0f && cw > 0f && ch > 0f) {
                        // Scale uniformly so the video covers the whole screen (centre crop).
                        val scale = max(cw / vw, ch / vh)
                        video.layoutParams = FrameLayout.LayoutParams(
                            (vw * scale).toInt(),
                            (vh * scale).toInt(),
                            Gravity.CENTER
                        )
                    }
                    video.start()
                }
                video.setOnCompletionListener { finish() }
                video.setOnErrorListener { _, _, _ ->
                    finish()
                    true
                }
                container
            },
            onRelease = { view ->
                (view.getChildAt(0) as? VideoView)?.stopPlayback()
            }
        )
    }
}

private const val SAFETY_TIMEOUT_MS = 8_000L
