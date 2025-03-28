package me.jinheng.cityullm.ui

import android.widget.Toast
import androidx.compose.animation.core.tween
import androidx.compose.animation.rememberSplineBasedDecay
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.KeyboardArrowRight
import androidx.compose.material.icons.outlined.KeyboardDoubleArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.jinheng.cityullm.R
import kotlin.math.roundToInt

private enum class DragAnchors(val fraction: Float) {
    Start(0f),
    End(1f),
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SwipeToStart(
    modifier: Modifier = Modifier,
    text: String = "Swipe to start...",
    onComplete: () -> Unit
) {
    val density = LocalDensity.current

    val decay = rememberSplineBasedDecay<Float>()

    val state = remember {
        AnchoredDraggableState<DragAnchors>(
            initialValue = DragAnchors.Start,
            positionalThreshold = { distance: Float -> distance * 0.5f },
            velocityThreshold = { with(density) { 100.dp.toPx() } },
            snapAnimationSpec = tween(),
            decayAnimationSpec = decay
        )
    }

    LaunchedEffect(state.currentValue) {
        if (state.currentValue == DragAnchors.End) {
            onComplete()
        }
    }

    val containerSize = 84.dp
    val contentPadding = 6.dp
    val contentSize = containerSize - contentPadding * 2
    val contentPaddingPx = with(density) { contentPadding.toPx() }
    val contentSizePx = with(density) { contentSize.toPx() }

    Box(
        modifier = modifier
            .height(containerSize)
            .fillMaxWidth()
            .onSizeChanged { layoutSize ->
                val dragEndPoint = layoutSize.width - contentSizePx - contentPaddingPx * 2
                state.updateAnchors(
                    DraggableAnchors {
                        DragAnchors.entries
                            .forEach { anchor ->
                                anchor at dragEndPoint * anchor.fraction
                            }
                    }
                )
            }
            .clip(shape = RoundedCornerShape(containerSize))
            .background(colorResource(R.color.mainColor))
            .padding(contentPadding)
    ) {
        Text(
            text,
            fontSize = 18.sp,
            color = Color.White,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .matchParentSize()
                .wrapContentHeight(align = Alignment.CenterVertically)
                .alpha(1 - state.offset / state.anchors.maxAnchor())
        )

        Box(
            modifier = Modifier
                .size(contentSize)
                .offset {
                    IntOffset(
                        x = state.requireOffset().roundToInt(),
                        y = 0,
                    )
                }
                .anchoredDraggable(state, Orientation.Horizontal)
                .clip(shape = CircleShape)
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                modifier = Modifier
                    .size(34.dp),
                imageVector = Icons.Outlined.KeyboardDoubleArrowRight,
                contentDescription = "",
                tint = colorResource(R.color.mainColor)
            )
        }
    }
}

@Preview
@Composable
fun SwipeToStartPreview() {
    SwipeToStart(
        onComplete = {}
    )
}
