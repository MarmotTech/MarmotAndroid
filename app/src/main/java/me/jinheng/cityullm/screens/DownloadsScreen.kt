package me.jinheng.cityullm.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import me.jinheng.cityullm.R
import me.jinheng.cityullm.models.ModelInfo
import me.jinheng.cityullm.models.ModelManager

@Composable
fun DownloadsScreen(
    modelManager: ModelManager,
    onHuggingFaceSheetToggle: (Boolean) -> Unit = {}
) {
    val coroutineScope = rememberCoroutineScope()
    var installedModels by remember { mutableStateOf(listOf<ModelInfo>()) }
    var missingModels by remember { mutableStateOf(listOf<ModelInfo>()) }

    val isDownloading by modelManager.isDownloading.collectAsState(false)
    val downloadProgress by modelManager.downloadProgress.collectAsState()

    LaunchedEffect(key1 = modelManager) {
        installedModels = modelManager.installedModels()
        missingModels = modelManager.missingModels()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(top = 24.dp)
            .padding(horizontal = 16.dp)
            .safeDrawingPadding()
    ) {
        Text(
            text = "MARMOT On-Device LLM",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = 16.dp)
        )

        if (isDownloading) {
            LinearProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp),
                progress = {
                    downloadProgress.toFloat() / 100f
                },
            )
        }

        // Installed Models Section
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(30.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(installedModels) { model ->
                InstalledModelItem(
                    model,
                    modelManager,
                    onUpdate = {
                        installedModels = modelManager.installedModels()
                        missingModels = modelManager.missingModels()
                    }
                )
            }

            // Divider
            item {
                HorizontalDivider()
            }

            items(missingModels) { model ->
                MissingModelItem(
                    model,
                    modelManager,
                    onUpdate = {
                        installedModels = modelManager.installedModels()
                        missingModels = modelManager.missingModels()
                    }
                )
            }

            item {
                HorizontalDivider()
            }

            item {
                Text(
                    "Didn't find what you're looking for? Try importing models from Hugging Face.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(30.dp))

                Button(
                    modifier = Modifier
                        .height(54.dp)
                        .fillMaxWidth(),
                    onClick = { onHuggingFaceSheetToggle(true) },
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(R.color.mainColor),
                    )
                ) {
                    Text("Import from Hugging Face", color = Color.White)
                }
            }
        }
    }
}

@Composable
fun InstalledModelItem(
    model: ModelInfo,
    modelManager: ModelManager,
    onUpdate: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(36.dp, 36.dp)
                .clip(CircleShape)
                .background(colorResource(R.color.tertiaryColor))
        ) {
            Image(
                painter = painterResource(id = R.drawable.tinyllama_icon),
                contentDescription = null,
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
            )
        }

        Spacer(modifier = Modifier.width(18.dp))

        Text(
            text = model.modelName,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )

        IconButton(
            onClick = {
                coroutineScope.launch {
                    modelManager.removeModels(models = listOf(model))
                    onUpdate()
                }
            }
        ) {
            Icon(
                imageVector = Icons.Filled.Delete,
                contentDescription = "Delete Icon",
                tint = Color.Red
            )
        }
    }
}

@Composable
fun MissingModelItem(
    model: ModelInfo,
    modelManager: ModelManager,
    onUpdate: () -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val modelDownloading by modelManager.modelDownloading(model = model).collectAsState(false)

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(36.dp, 36.dp)
                .clip(CircleShape)
                .background(colorResource(R.color.tertiaryColor))
        ) {
            Image(
                painter = painterResource(id = R.drawable.tinyllama_icon),
                contentDescription = null,
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape)
            )
        }

        Spacer(modifier = Modifier.width(18.dp))

        Text(
            text = model.modelName,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )

        IconButton(
            onClick = {
                coroutineScope.launch {
                    modelManager.downloadModels(models = listOf(model))
                    onUpdate()
                }
            },
            enabled = !modelDownloading
        ) {
            Icon(
                imageVector = Icons.Filled.ArrowDownward,
                contentDescription = "Download Icon",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Preview(name = "DownloadsScreenPreview")
@Composable
fun DownloadsScreenPreview() {
    DownloadsScreen(modelManager = ModelManager())
}
