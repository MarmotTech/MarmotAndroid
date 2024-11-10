package me.jinheng.cityullm.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.jinheng.cityullm.ChatActivity
import me.jinheng.cityullm.R
import me.jinheng.cityullm.ui.NoChatsPlaceholder
import me.jinheng.cityullm.models.ModelInfo
import me.jinheng.cityullm.utils.ModelOperations

@Composable
fun ModelsScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(top = 24.dp)
            .safeDrawingPadding()
    ) {
        Column(
            Modifier
                .padding(horizontal = 16.dp)
        ) {
            Text(
                "MBZUAI On-Device LLM",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )

            Row(
                modifier = Modifier
                    .padding(top = 50.dp, bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(30.dp)
            ) {
                ModelOperations.allSupportModels.forEach {
                    ModelItem(it)
                }
            }
        }

        HorizontalDivider(
            color = colorResource(R.color.tertiaryColor),
            thickness = 2.dp
        )

        NoChatsPlaceholder(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
        )
    }
}

@Composable
fun ModelItem(modelInfo: ModelInfo) {
    val context = LocalContext.current

    Button(
        onClick = {
            val intent = Intent(context, ChatActivity::class.java)
            intent.putExtra("modelName", modelInfo.modelName)

            context.startActivity(intent)
        },
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
                modifier = Modifier
                    .size(64.dp, 64.dp)
                    .clip(CircleShape)
                    .background(colorResource(R.color.tertiaryColor))
            ) {

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
            tasks = null
        )
    )
}

@Preview(name = "ModelsScreenPreview")
@Composable
fun ModelsScreenPreview() {
    ModelsScreen()
}
