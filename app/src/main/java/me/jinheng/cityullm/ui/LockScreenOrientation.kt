package me.jinheng.cityullm.ui

import android.content.pm.ActivityInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
import me.jinheng.cityullm.utils.requireActivity

@Composable
fun LockScreenOrientation() {
    val context = LocalContext.current
    DisposableEffect(context) {
        context.requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LOCKED
        onDispose {
            context.requireActivity().requestedOrientation =
                ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
    }
}
