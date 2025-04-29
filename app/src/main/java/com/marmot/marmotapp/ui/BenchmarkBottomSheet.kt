package com.marmot.marmotapp.ui

import android.graphics.Typeface
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetDefaults
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.marmot.marmotapp.R
import com.marmot.marmotapp.models.BenchmarkResult
import com.marmot.marmotapp.models.LLama
import com.marmot.marmotapp.models.ModelInfo
import com.marmot.marmotapp.models.ModelManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BenchmarkBottomSheet(
    modelManager: ModelManager,
    onComplete: (List<BenchmarkResult>?) -> Unit
) {
    var isRunning by remember { mutableStateOf(false) }
    val selectedModels = remember { mutableStateListOf<ModelInfo>() }
    val selectedTasks = remember { mutableStateListOf<String>() }

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = { it != SheetValue.Hidden || !isRunning },
    )

    ModalBottomSheet(
        onDismissRequest = {
            if (!isRunning) {
                onComplete(null)
            }
        },
        sheetState = sheetState,
        containerColor = Color.White,
        dragHandle = {},
        properties = ModalBottomSheetDefaults.properties(
            shouldDismissOnBackPress = !isRunning
        ),
        windowInsets = WindowInsets(0, 0, 0, 0)
    ) {
        if (isRunning) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    "Benchmark in Progress...",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                )

                Text(
                    "Please do not close the app and wait for a while.",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )

                HandsLoader(
                    modifier = Modifier
                        .size(200.dp, 200.dp)
                        .align(Alignment.CenterHorizontally)
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    "Initiating New Benchmark Process",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                )

                Text(
                    "Begin the benchmarking setup.",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )

                Spacer(
                    modifier = Modifier
                        .height(24.dp)
                )

                Text(
                    "Choose the Model(s) to Benchmark:",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    "Select the models you want to evaluate.",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Gray
                )

                LazyColumn {
                    items(modelManager.installedModels()) { modelInfo ->
                        val modelSelected = selectedModels.contains(modelInfo)

                        SelectableRow(
                            selected = modelSelected,
                            text = modelInfo.modelName,
                            onToggle = {
                                if (modelSelected) {
                                    selectedModels.remove(modelInfo)
                                    selectedTasks.clear()
                                } else {
                                    selectedModels.add(modelInfo)
                                }
                            }
                        )
                    }
                }

                if (selectedModels.isNotEmpty()) {
                    HorizontalDivider(
                        color = colorResource(R.color.tertiaryColor),
                        thickness = 2.dp
                    )

                    Spacer(
                        modifier = Modifier
                            .height(16.dp)
                    )

                    Text(
                        "Select the Task(s) for Evaluation:",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        "Choose the tasks for the benchmarking process.",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray
                    )

                    LazyColumn {
                        items(
                            selectedModels
                                .flatMap { it.tasks ?: arrayListOf() }
                                .distinct()
                        ) { task ->
                            val taskSelected = selectedTasks.contains(task)

                            SelectableRow(
                                selected = taskSelected,
                                text = task,
                                onToggle = {
                                    if (taskSelected) {
                                        selectedTasks.remove(task)
                                    } else {
                                        selectedTasks.add(task)
                                    }
                                }
                            )
                        }
                    }
                }

                Spacer(
                    modifier = Modifier
                        .height(24.dp)
                )

                Box(
                    modifier = Modifier
                        .navigationBarsPadding()
                ) {
                    Button(
                        modifier = Modifier
                            .height(54.dp)
                            .fillMaxWidth(),
                        enabled = selectedTasks.isNotEmpty(),
                        onClick = {
                            isRunning = true
                            LLama.startBenchmark(
                                models = selectedModels.map { it.modelLocalPath }.toTypedArray(),
                                tasks = selectedTasks.toTypedArray(),
                                onFinished = onComplete
                            )
                        },
                        shape = RoundedCornerShape(24.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(R.color.mainColor),
                        ),
                    ) {
                        Text(
                            "Start benchmark",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SelectableRow(
    text: String,
    selected: Boolean,
    onToggle: () -> Unit
) {
    Row(
        modifier = Modifier
            .padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Button(
            modifier = Modifier
                .size(24.dp, 24.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (selected) colorResource(R.color.mainColor) else colorResource(
                    R.color.tertiaryColor
                )
            ),
            contentPadding = PaddingValues(0.dp),
            onClick = onToggle
        ) {
            if (selected) {
                Icon(
                    modifier = Modifier
                        .size(16.dp, 16.dp),
                    imageVector = Icons.Outlined.Check,
                    contentDescription = "",
                    tint = Color.White
                )
            }
        }

        Text(
            text,
            fontSize = 14.sp,
            fontFamily = FontFamily(Typeface.MONOSPACE)
        )
    }
}