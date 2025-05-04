package com.marmot.marmotapp.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.marmot.marmotapp.R
import com.marmot.marmotapp.models.ModelInfo
import coil.compose.rememberAsyncImagePainter

@Composable
fun ModelImage(
    modelInfo: ModelInfo,
    modifier: Modifier
) {
    Image(
        painter = rememberAsyncImagePainter(modelInfo.logoPath),
        contentDescription = null,
        modifier = modifier
            .clip(CircleShape)
    )
}
