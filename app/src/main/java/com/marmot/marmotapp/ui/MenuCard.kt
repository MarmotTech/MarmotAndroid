package com.marmot.marmotapp.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.marmot.marmotapp.R

class AsymmetricCutCornerShape(
    private val cornerRadius: Dp,
    private val innerCornerRadius: Dp,
    private val cutCornerHeightPx: Float,
    private val cutCornerWidthPx: Float
) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path().apply {
            val cornerRadiusPx = with(density) { cornerRadius.toPx() }
            val innerCornerRadiusPx = with(density) { innerCornerRadius.toPx() }

            moveTo(0f, cornerRadiusPx)
            arcTo(
                Rect(0f, 0f, 2 * cornerRadiusPx, 2 * cornerRadiusPx),
                180f,
                90f,
                false
            )

            lineTo(size.width - cornerRadiusPx, 0f)
            arcTo(
                Rect(size.width - 2 * cornerRadiusPx, 0f, size.width, 2 * cornerRadiusPx),
                270f,
                90f,
                false
            )

            lineTo(size.width, size.height - cutCornerHeightPx - cornerRadiusPx)
            arcTo(
                Rect(
                    size.width - 2 * cornerRadiusPx,
                    size.height - cutCornerHeightPx - 2 * cornerRadiusPx,
                    size.width,
                    size.height - cutCornerHeightPx
                ),
                0f,
                90f,
                false
            )

            lineTo(
                size.width - cutCornerWidthPx + innerCornerRadiusPx,
                size.height - cutCornerHeightPx
            )

            arcTo(
                Rect(
                    size.width - cutCornerWidthPx,
                    size.height - cutCornerHeightPx,
                    size.width - cutCornerWidthPx + 2 * innerCornerRadiusPx,
                    size.height - cutCornerHeightPx + 2 * innerCornerRadiusPx
                ),
                270f,
                -90f,
                false
            )

            lineTo(
                size.width - cutCornerWidthPx,
                size.height - cornerRadiusPx
            )

            arcTo(
                Rect(
                    size.width - cutCornerWidthPx - 2 * cornerRadiusPx,
                    size.height - 2 * cornerRadiusPx,
                    size.width - cutCornerWidthPx,
                    size.height
                ),
                0f,
                90f,
                false
            )

            lineTo(cornerRadiusPx, size.height)
            arcTo(
                Rect(0f, size.height - 2 * cornerRadiusPx, 2 * cornerRadiusPx, size.height),
                90f,
                90f,
                false
            )

            close()
        }
        return Outline.Generic(path)
    }
}

@Composable
fun MenuCard(
    onStartClick: () -> Unit,
    title: String,
    description: String,
    buttonText: String,
    containerColor: Color,
    buttonColors: ButtonColors,
    modifier: Modifier = Modifier
) {
    var cutCornerWidth by remember { mutableFloatStateOf(140f) }
    var cutCornerHeight by remember { mutableFloatStateOf(68f) }

    val density = LocalDensity.current
    val buttonPaddingPx = with(density) { 12.dp.toPx() }

    Box(modifier = modifier) {
        Card(
            shape = AsymmetricCutCornerShape(
                cornerRadius = 24.dp,
                innerCornerRadius = 36.dp,
                cutCornerHeightPx = cutCornerHeight,
                cutCornerWidthPx = cutCornerWidth,
            ),
            colors = CardDefaults.cardColors(
                containerColor = containerColor
            )
        ) {
            Box {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = title,
                        fontSize = 28.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        lineHeight = 36.sp
                    )

                    Text(
                        text = description,
                        fontSize = 16.sp,
                        color = Color.White.copy(alpha = 0.9f),
                        modifier = Modifier.padding(
                            top = 8.dp,
                            bottom = 68.dp
                        )
                    )
                }
            }
        }

        Button(
            modifier = Modifier
                .height(54.dp)
                .align(Alignment.BottomEnd)
                .onSizeChanged {
                    cutCornerWidth = it.width + buttonPaddingPx
                    cutCornerHeight = it.height + buttonPaddingPx
                },
            onClick = onStartClick,
            shape = RoundedCornerShape(24.dp),
            colors = buttonColors,
        ) {
            Text(
                buttonText
            )
        }
    }
}

@Preview
@Composable
fun MenuCardPreview() {
    MenuCard(
        title = "Start Message With any popular LLM you want",
        description = "Ask anything and LLM will be ready to proceed locally and answer you",
        buttonText = "Start Benchmarking",
        containerColor = colorResource(R.color.mainColor),
        onStartClick = {},
        buttonColors = ButtonDefaults.buttonColors(
            containerColor = colorResource(R.color.secondaryColor),
            contentColor = Color.White
        )
    )
}