package me.jinheng.cityullm.ui

import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem.*
import androidx.media3.common.Player.REPEAT_MODE_ALL
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView

@Composable
fun HandsLoader(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val mediaItem = Builder()
        .setUri("asset:///loader.mp4")
        .build()
    val exoPlayer = remember(context, mediaItem) {
        ExoPlayer.Builder(context)
            .build()
            .also { exoPlayer ->
                exoPlayer.setMediaItem(mediaItem)
                exoPlayer.prepare()
                exoPlayer.playWhenReady = true
                exoPlayer.repeatMode = REPEAT_MODE_ALL
            }
    }

    AndroidView(
        modifier = modifier,
        factory = {
            PlayerView(context).apply {
                player = exoPlayer
                useController = false
                layoutParams =
                    FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams
                            .MATCH_PARENT,
                        ViewGroup.LayoutParams
                            .MATCH_PARENT
                    )
            }
        }
    )

    DisposableEffect(
        Unit
    ) {
        onDispose { exoPlayer.release() }
    }
}
