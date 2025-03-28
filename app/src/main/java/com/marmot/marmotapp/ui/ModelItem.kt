package com.marmot.marmotapp.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.marmot.marmotapp.R
import com.marmot.marmotapp.models.ModelInfo
import coil.compose.rememberAsyncImagePainter

@Composable
fun ModelItem(
    modelInfo: ModelInfo,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent
        ),
        contentPadding = PaddingValues(0.dp),
        shape = RectangleShape
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(64.dp, 64.dp)
                    .clip(CircleShape)
                    .background(colorResource(R.color.tertiaryColor))
            ) {
                ModelImage(
                    modelInfo = modelInfo,
                    modifier = Modifier
                        .size(32.dp)
                )
            }

            Text(
                modelInfo.modelName,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Black
            )
        }
    }
}

@Preview(name = "ModelItemPreview")
@Composable
fun ModelItemPreview() {
    ModelItem(
        modelInfo = ModelInfo(
            modelName = "LLama",
            modelUrl = "",
            modelLocalPath = "",
            modelSize = 0,
            prefetchSize = 0,
            systemPrompt = "",
            kvSize = 0,
            tasks = null,
            logoPath = ""
        ),
        onClick = {}
    )
}