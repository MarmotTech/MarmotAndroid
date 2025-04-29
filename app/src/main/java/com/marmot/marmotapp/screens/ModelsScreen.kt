package com.marmot.marmotapp.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.marmot.marmotapp.ChatActivity
import com.marmot.marmotapp.DownloadsActivity
import com.marmot.marmotapp.R
import com.marmot.marmotapp.models.ModelManager
import com.marmot.marmotapp.ui.NoChatsPlaceholder
import com.marmot.marmotapp.ui.ModelItem

@Composable
fun ModelsScreen(
    modelManager: ModelManager
) {
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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    "MARMOT On-Device LLM",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(vertical = 16.dp)
                        .weight(1f)
                )

                Button(
                    modifier = Modifier
                        .size(width = 32.dp, height = 32.dp),
                    contentPadding = PaddingValues(0.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent
                    ),
                    onClick = {
                        val intent = Intent(context, DownloadsActivity::class.java)
                        context.startActivity(intent)
                    }
                ) {
                    Icon(
                        modifier = Modifier
                            .size(24.dp, 24.dp),
                        imageVector = Icons.Outlined.Settings,
                        contentDescription = "",
                        tint = Color.Black
                    )
                }
            }

            LazyRow(
                modifier = Modifier
                    .padding(bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(30.dp)
            ) {
                items(modelManager.installedModels()) { it ->
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
    ModelsScreen(
        modelManager = ModelManager()
    )
}
