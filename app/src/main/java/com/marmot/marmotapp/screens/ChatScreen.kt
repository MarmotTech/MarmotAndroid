package com.marmot.marmotapp.screens

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.marmot.marmotapp.R
import com.marmot.marmotapp.SettingsActivity
import com.marmot.marmotapp.models.ChatItem
import com.marmot.marmotapp.models.ChatItemType
import com.marmot.marmotapp.models.LLama
import com.marmot.marmotapp.models.ModelInfo
import com.marmot.marmotapp.utils.advancedShadow

@Composable
fun ChatScreen(modelInfo: ModelInfo) {
    val context = LocalContext.current
    var textValue by remember { mutableStateOf("") }
    val history = remember { mutableStateListOf<ChatItem>() }
    val scrollState = rememberScrollState()
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(Unit) {
        LLama.init(
            modelInfo,
            true,
            object: LLama.ChatListener {
                override fun onUpdateInfo(s: String) {
                    println(s)
                }

                override fun onBotContinue(s: String) {
                    history[history.size - 1] = history[history.size - 1].appendText(s)
                }
            }
        )
    }

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
                modifier = Modifier
                    .weight(1f),
                text = modelInfo.modelName,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )

            Button(
                modifier = Modifier
                    .size(width = 32.dp, height = 32.dp),
                contentPadding = PaddingValues(0.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent
                ),
                onClick = {
                    val intent = Intent(context, SettingsActivity::class.java)
                    intent.putExtra("modelName", modelInfo.modelName)

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

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(colorResource(R.color.tertiaryColor))
        ) {
            LazyColumn (
                modifier = Modifier
                    .fillMaxSize(),
                reverseLayout = true,
                contentPadding = PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    top = 16.dp,
                    bottom = 120.dp
                ),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                history.reversed().forEach {
                    item {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentWidth(
                                    if (it.type == ChatItemType.BotMessage) Alignment.Start else Alignment.End
                                )
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(24.dp))
                                    .background(
                                        if (it.type == ChatItemType.BotMessage) Color.White else colorResource(
                                            R.color.mainColor
                                        )
                                    )
                                    .padding(16.dp)
                                    .widthIn(0.dp, 260.dp)
                            ) {
                                Text(
                                    it.text,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (it.type == ChatItemType.BotMessage) Color.Black else Color.White
                                )
                            }
                        }
                    }
                }
            }

            Row(
                Modifier
                    .padding(32.dp)
                    .imePadding()
                    .advancedShadow(
                        color = Color.Black,
                        alpha = 0.1f,
                        cornersRadius = 62.dp,
                        shadowBlurRadius = 10.dp,
                        offsetY = 0.dp,
                        offsetX = 0.dp
                    )
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .height(62.dp)
                    .clip(RoundedCornerShape(62.dp))
                    .background(Color.White)
                    .padding(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp)
                ) {
                    if (textValue.isEmpty()) {
                        Text(
                            "Type message...",
                            fontSize = 16.sp,
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                        )
                    }

                    BasicTextField(
                        value = textValue,
                        onValueChange = {
                            textValue = it
                        },
                        singleLine = true,
                        visualTransformation = VisualTransformation.None,
                        textStyle = TextStyle(
                            fontSize = 16.sp
                        ),
                        modifier = Modifier
                            .fillMaxSize()
                            .wrapContentHeight(align = Alignment.CenterVertically)
                    )
                }

                Button (
                    onClick = {
                        history.add(
                            ChatItem(
                                type = ChatItemType.UserMessage,
                                text = textValue
                            )
                        )
                        history.add(
                            ChatItem(
                                type = ChatItemType.BotMessage,
                                text = ""
                            )
                        )
                        LLama.run(textValue)
                        textValue = ""
                        keyboardController?.hide()
                    },
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(R.color.mainColor)
                    ),
                    contentPadding = PaddingValues(0.dp),
                    modifier = Modifier
                        .size(46.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.send_fill),
                        contentDescription = "",
                        tint = Color.White,
                        modifier = Modifier
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun ChatScreenPreview() {
    ChatScreen(
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
        )
    )
}
