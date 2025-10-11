package com.marmot.marmotapp.screens

import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.marmot.marmotapp.ChatActivity
import com.marmot.marmotapp.DownloadsActivity
import com.marmot.marmotapp.R
import com.marmot.marmotapp.SettingsActivity
import com.marmot.marmotapp.models.ChatHistory
import com.marmot.marmotapp.models.ModelManager
import com.marmot.marmotapp.models.ModelInfo
import com.marmot.marmotapp.ui.ChatHistoryItem
import com.marmot.marmotapp.ui.ModelItem
import com.marmot.marmotapp.ui.NoChatsPlaceholder

@Composable
fun ModelsScreen(
    modelManager: ModelManager
) {
    val context = LocalContext.current
    var selectedModel by remember { mutableStateOf<ModelInfo?>(null) }
    var threadNum by remember { mutableIntStateOf(2) }
    var memorySize by remember { mutableIntStateOf(2) }

    val cpuCount = Runtime.getRuntime().availableProcessors()
    val maxMemory = 8

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = listOf(
                        Color(0xFFFAFAFF),
                        Color.White
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .safeDrawingPadding()
        ) {
            // Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFFFAFAFF),
                                Color.White
                            )
                        )
                    )
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Select Model",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorResource(R.color.secondaryColor)
                        )

                        Text(
                            text = "${modelManager.installedModels().size} models available",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    IconButton(
                        onClick = {
                            context.startActivity(Intent(context, SettingsActivity::class.java))
                        },
                        modifier = Modifier
                            .size(44.dp)
                            .background(Color.White.copy(alpha = 0.8f), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Settings,
                            contentDescription = "Settings",
                            tint = Color.Gray,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                }

                // Model selection row
                if (modelManager.installedModels().isNotEmpty()) {
                    LazyRow(
                        modifier = Modifier.padding(top = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(modelManager.installedModels()) { model ->
                            ModernModelCard(
                                model = model,
                                isSelected = selectedModel?.modelName == model.modelName,
                                onClick = {
                                    selectedModel = if (selectedModel?.modelName == model.modelName) null else model
                                }
                            )
                        }
                    }
                }
            }

            // Content area
            selectedModel?.let { model ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    // Configuration Section
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 20.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Configuration",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = colorResource(R.color.secondaryColor)
                            )

                            Text(
                                text = model.modelName.take(20),
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }

                        // Thread configuration
                        ConfigurationRow(
                            icon = R.drawable.cpu,
                            label = "Threads",
                            value = threadNum,
                            onDecrease = { if (threadNum > 1) threadNum-- },
                            onIncrease = { if (threadNum < cpuCount) threadNum++ },
                            canDecrease = threadNum > 1,
                            canIncrease = threadNum < cpuCount
                        )

                        // Memory configuration
                        ConfigurationRow(
                            icon = R.drawable.memory,
                            label = "Memory",
                            value = memorySize,
                            onDecrease = { if (memorySize > 1) memorySize-- },
                            onIncrease = { if (memorySize < maxMemory) memorySize++ },
                            canDecrease = memorySize > 1,
                            canIncrease = memorySize < maxMemory,
                            suffix = "GB"
                        )
                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 8.dp)
                    )

                    // Recent Sessions
                    val chatSummaries = remember { ChatHistory.getAllChatSummaries(context) }
                    val modelSessions = chatSummaries.filter { it.modelName == model.modelName }

                    if (modelSessions.isNotEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp)
                        ) {
                            Text(
                                text = "Continue Conversation",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = colorResource(R.color.secondaryColor),
                                modifier = Modifier.padding(bottom = 16.dp)
                            )

                            modelSessions.take(5).forEach { chatSummary ->
                                Card(
                                    onClick = {
                                        val intent = Intent(context, ChatActivity::class.java)
                                        intent.putExtra("chatId", chatSummary.id)
                                        context.startActivity(intent)
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 12.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = Color.White
                                    ),
                                    elevation = CardDefaults.cardElevation(
                                        defaultElevation = 2.dp
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = if (chatSummary.lastMessage.isNotEmpty())
                                                    chatSummary.lastMessage
                                                else
                                                    "New conversation",
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Medium,
                                                color = colorResource(R.color.secondaryColor),
                                                maxLines = 1
                                            )

                                            Text(
                                                text = chatSummary.modelName,
                                                fontSize = 12.sp,
                                                color = Color.Gray,
                                                modifier = Modifier.padding(top = 4.dp)
                                            )
                                        }

                                        Icon(
                                            imageVector = Icons.Default.Message,
                                            contentDescription = null,
                                            tint = Color.Gray,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(100.dp))
                }
            } ?: run {
                // Empty state
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Message,
                        contentDescription = null,
                        tint = Color.Gray.copy(alpha = 0.3f),
                        modifier = Modifier.size(50.dp)
                    )

                    Text(
                        text = "Select a model to start chatting",
                        fontSize = 18.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 20.dp)
                    )
                }
            }
        }

        // Floating action button
        selectedModel?.let {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(20.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Settings summary
                Card(
                    modifier = Modifier.shadow(5.dp, CircleShape),
                    shape = CircleShape,
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.cpu),
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = Color.Gray
                        )
                        Text(
                            text = "$threadNum",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Gray
                        )

                        HorizontalDivider(
                            modifier = Modifier
                                .width(1.dp)
                                .height(16.dp),
                            color = Color.Gray.copy(alpha = 0.3f)
                        )

                        Icon(
                            painter = painterResource(R.drawable.memory),
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = Color.Gray
                        )
                        Text(
                            text = "${memorySize}GB",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Gray
                        )
                    }
                }

                // Start Chat button
                FloatingActionButton(
                    onClick = {
                        val intent = Intent(context, ChatActivity::class.java)
                        intent.putExtra("modelName", selectedModel?.modelName)
                        context.startActivity(intent)
                    },
                    containerColor = colorResource(R.color.mainColor),
                    modifier = Modifier.shadow(10.dp, CircleShape)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 24.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Message,
                            contentDescription = "Start Chat",
                            tint = Color.White
                        )
                        Text(
                            text = "Start Chat",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ModernModelCard(
    model: ModelInfo,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .width(180.dp)
            .height(120.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) colorResource(R.color.mainColor) else Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 10.dp else 5.dp
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Icon(
                    painter = painterResource(R.drawable.cpu),
                    contentDescription = null,
                    tint = if (isSelected) Color.White else colorResource(R.color.mainColor),
                    modifier = Modifier.size(24.dp)
                )

                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Selected",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Text(
                text = model.modelName,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isSelected) Color.White else colorResource(R.color.secondaryColor),
                maxLines = 2
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "•",
                    fontSize = 12.sp,
                    color = if (isSelected) Color.White.copy(alpha = 0.9f) else Color.Gray
                )
                Text(
                    text = "${model.modelSize / (1024 * 1024)} MB",
                    fontSize = 12.sp,
                    color = if (isSelected) Color.White.copy(alpha = 0.9f) else Color.Gray
                )
            }
        }
    }
}

@Composable
fun ConfigurationRow(
    icon: Int,
    label: String,
    value: Int,
    onDecrease: () -> Unit,
    onIncrease: () -> Unit,
    canDecrease: Boolean,
    canIncrease: Boolean,
    suffix: String = ""
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(icon),
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = colorResource(R.color.secondaryColor)
                )
                Text(
                    text = label,
                    fontSize = 15.sp,
                    color = colorResource(R.color.secondaryColor)
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onDecrease,
                    enabled = canDecrease,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = "Decrease",
                        tint = if (canDecrease) colorResource(R.color.mainColor) else Color.Gray.copy(alpha = 0.3f),
                        modifier = Modifier.size(24.dp)
                    )
                }

                Text(
                    text = "$value$suffix",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = colorResource(R.color.secondaryColor),
                    modifier = Modifier.width(45.dp)
                )

                IconButton(
                    onClick = onIncrease,
                    enabled = canIncrease,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Increase",
                        tint = if (canIncrease) colorResource(R.color.mainColor) else Color.Gray.copy(alpha = 0.3f),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

@Preview(name = "ModelsScreenPreview")
@Composable
fun ModelsScreenPreview() {
    ModelsScreen(
        modelManager = ModelManager()
    )
}
