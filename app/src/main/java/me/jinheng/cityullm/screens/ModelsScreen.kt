package me.jinheng.cityullm.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.jinheng.cityullm.ChatActivity
import me.jinheng.cityullm.R
import me.jinheng.cityullm.ui.NoChatsPlaceholder
import me.jinheng.cityullm.ui.ModelItem
import me.jinheng.cityullm.utils.ModelOperations

@Composable
fun ModelsScreen() {
    val context = LocalContext.current

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
                "MARMOT On-Device LLM",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(vertical = 16.dp)
            )

            Row(
                modifier = Modifier
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(30.dp)
            ) {
                ModelOperations.allSupportModels.forEach {
                    ModelItem(
                        modelInfo = it,
                        onClick = {
                            val intent = Intent(context, ChatActivity::class.java)
                            intent.putExtra("modelName", it.modelName)

                            context.startActivity(intent)
                        },
                    )
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

@Preview(name = "ModelsScreenPreview")
@Composable
fun ModelsScreenPreview() {
    ModelsScreen()
}
