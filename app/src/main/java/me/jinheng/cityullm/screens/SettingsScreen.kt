package me.jinheng.cityullm.screens

import android.app.ActivityManager
import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.jinheng.cityullm.R
import me.jinheng.cityullm.models.Config
import me.jinheng.cityullm.models.ModelInfo


@Composable
fun SettingsScreen(
    modelInfo: ModelInfo
) {
    val context = LocalContext.current

    val cpuCount = remember { Runtime.getRuntime().availableProcessors() }
    val maxMemory = remember {
        val memInfo = ActivityManager.MemoryInfo()
        val activityManager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        activityManager.getMemoryInfo(memInfo)
        val totalMemory = memInfo.totalMem

        totalMemory / (1024 * 1024 * 1024)
    }

    var threadNum by remember { mutableIntStateOf(Config.threadNum) }
    var memorySize by remember { mutableIntStateOf(Config.maxMemorySize) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(top = 24.dp)
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .padding(bottom = 16.dp)
                .statusBarsPadding(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(64.dp, 64.dp)
                    .clip(CircleShape)
                    .background(colorResource(R.color.tertiaryColor))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.tinyllama_icon),
                    contentDescription = null,
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                )
            }

            Text(
                modelInfo.modelName,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(colorResource(R.color.tertiaryColor))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Settings",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                )

                SettingsNumRow(
                    text = "Threads Limit",
                    icon = painterResource(R.drawable.cpu),
                    value = threadNum,
                    onChange = {
                        if (it in 2..cpuCount) {
                            threadNum = it
                            Config.threadNum = it
                        }
                    }
                )
                SettingsNumRow(
                    text = "Memory Limit",
                    icon = painterResource(R.drawable.memory),
                    value = memorySize,
                    onChange = {
                        if (it in 1..maxMemory) {
                            memorySize = it
                            Config.maxMemorySize = it
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun SettingsNumRow(
    text: String,
    icon: Painter,
    value: Int,
    onChange: (Int) -> Unit
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(24.dp))
            .background(Color.White)
            .padding(16.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Image(
            modifier = Modifier
                .size(24.dp, 24.dp),
            painter = icon,
            contentDescription = ""
        )

        Text(text)

        Spacer(
            modifier = Modifier.weight(1f)
        )

        Button(
            modifier = Modifier
                .size(width = 32.dp, height = 32.dp),
            contentPadding = PaddingValues(0.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent
            ),
            onClick = {
                onChange(value - 1)
            }
        ) {
            Icon(
                modifier = Modifier
                    .size(24.dp, 24.dp),
                imageVector = Icons.Outlined.Remove,
                contentDescription = "",
                tint = Color.Black
            )
        }

        Text("$value")

        Button(
            modifier = Modifier
                .size(width = 32.dp, height = 32.dp),
            contentPadding = PaddingValues(0.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.Transparent
            ),
            onClick = {
                onChange(value + 1)
            }
        ) {
            Icon(
                modifier = Modifier
                    .size(24.dp, 24.dp),
                imageVector = Icons.Outlined.Add,
                contentDescription = "",
                tint = Color.Black
            )
        }
    }
}
