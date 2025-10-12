package com.marmot.marmotapp.screens

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
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
import com.marmot.marmotapp.R
import com.marmot.marmotapp.models.BenchmarkResult
import com.marmot.marmotapp.models.BenchmarkResultsDatabase
import com.marmot.marmotapp.models.BenchmarkResultsRepository
import com.marmot.marmotapp.models.ModelManager
import com.marmot.marmotapp.ui.BenchmarkBottomSheet
import java.util.Locale

@Composable
fun BenchmarkScreen(
    modelManager: ModelManager
) {
    var showCreateSheet by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val resultsRepository = remember {
        BenchmarkResultsRepository(
            BenchmarkResultsDatabase.getDatabase(context).benchmarkResultsDao()
        )
    }
    val benchmarkResults by resultsRepository.benchmarkResults.observeAsState(listOf())

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
            // Header Section
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
                            "Benchmarks",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = colorResource(R.color.secondaryColor)
                        )

                        Text(
                            "${benchmarkResults.size} results",
                            fontSize = 14.sp,
                            color = Color.Gray,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }

            // Content Area
            if (benchmarkResults.isEmpty()) {
                // Empty State
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        painter = painterResource(R.drawable.cpu),
                        contentDescription = null,
                        tint = Color.Gray.copy(alpha = 0.3f),
                        modifier = Modifier.size(50.dp)
                    )

                    Text(
                        "No benchmark results yet",
                        fontSize = 18.sp,
                        color = Color.Gray,
                        modifier = Modifier.padding(top = 20.dp)
                    )

                    Text(
                        "Create a benchmark to compare model performance",
                        fontSize = 14.sp,
                        color = Color.Gray.copy(alpha = 0.8f),
                        modifier = Modifier.padding(top = 8.dp, start = 40.dp, end = 40.dp)
                    )
                }
            } else {
                // Benchmark Results List
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(
                        horizontal = 20.dp,
                        vertical = 16.dp
                    ),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(benchmarkResults) { result ->
                        BenchmarkResultCard(
                            result = result,
                            modelManager = modelManager
                        )
                    }
                    // Add spacing for FABs
                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }

        // Floating Action Buttons
        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (benchmarkResults.isNotEmpty()) {
                // Export Button
                FloatingActionButton(
                    onClick = { /* TODO: Implement export */ },
                    containerColor = Color.White,
                    contentColor = colorResource(R.color.secondaryColor),
                    elevation = FloatingActionButtonDefaults.elevation(
                        defaultElevation = 5.dp
                    ),
                    shape = CircleShape,
                    modifier = Modifier.shadow(5.dp, CircleShape)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Export"
                        )
                        Text(
                            "Export",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // New Benchmark Button
            FloatingActionButton(
                onClick = { showCreateSheet = true },
                containerColor = colorResource(R.color.mainColor),
                contentColor = Color.White,
                elevation = FloatingActionButtonDefaults.elevation(
                    defaultElevation = 10.dp
                ),
                shape = CircleShape,
                modifier = Modifier.shadow(10.dp, CircleShape)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "New Benchmark"
                    )
                    Text(
                        "New Benchmark",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        if (showCreateSheet) {
            BenchmarkBottomSheet(
                modelManager = modelManager,
                onComplete = {
                    showCreateSheet = false
                    if (it != null) {
                        resultsRepository.addResults(it)
                    }
                }
            )
        }
    }
}

@Composable
fun BenchmarkResultCard(
    result: BenchmarkResult,
    modelManager: ModelManager
) {
    var isExpanded by remember { mutableStateOf(false) }
    val rotationAngle by animateFloatAsState(
        targetValue = if (isExpanded) 180f else 0f,
        label = "rotation"
    )

    val modelInfo = modelManager.installedModels().firstOrNull {
        it.modelLocalPath == result.model_name
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 5.dp
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column {
            // Card Header (Always visible)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded }
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        modelInfo?.modelName ?: result.model_name,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = colorResource(R.color.secondaryColor)
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // Decode throughput preview
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.cpu),
                                contentDescription = null,
                                tint = Color.Gray,
                                modifier = Modifier.size(12.dp)
                            )
                            Text(
                                String.format(Locale.ROOT, "%.1f t/s", result.decode_throughput),
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }

                        // First task result preview
                        result.task_results.entries.firstOrNull()?.let { (taskName, taskValue) ->
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.send_fill),
                                    contentDescription = null,
                                    tint = Color.Gray,
                                    modifier = Modifier.size(12.dp)
                                )
                                Text(
                                    "$taskName: ${String.format(Locale.ROOT, "%.3f", taskValue)}",
                                    fontSize = 12.sp,
                                    color = Color.Gray
                                )
                            }
                        }
                    }
                }

                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (isExpanded) "Collapse" else "Expand",
                    tint = colorResource(R.color.mainColor).copy(alpha = 0.8f),
                    modifier = Modifier
                        .size(24.dp)
                        .rotate(rotationAngle)
                )
            }

            // Expanded Content
            if (isExpanded) {
                Divider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = Color.Gray.copy(alpha = 0.2f)
                )

                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Task Results
                    if (result.task_results.isNotEmpty()) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                "Task Results",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.Gray
                            )

                            result.task_results.entries.forEach { (taskName, taskValue) ->
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        taskName,
                                        fontSize = 14.sp,
                                        color = colorResource(R.color.secondaryColor)
                                    )
                                    Text(
                                        String.format(Locale.ROOT, "%.3f", taskValue),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = colorResource(R.color.mainColor)
                                    )
                                }
                            }
                        }
                    }

                    // Performance Metrics
                    Column(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            "Performance",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.Gray
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            MetricPill(
                                icon = R.drawable.send_fill,
                                label = "Decode",
                                value = String.format(Locale.ROOT, "%.1f t/s", result.decode_throughput),
                                color = colorResource(R.color.mainColor)
                            )

                            MetricPill(
                                icon = R.drawable.send_fill,
                                label = "Prefill",
                                value = String.format(Locale.ROOT, "%.1f t/s", result.prefill_throughput),
                                color = colorResource(R.color.mainColor)
                            )
                        }
                    }

                    // Model Info
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        MetricPill(
                            icon = R.drawable.cpu,
                            label = "Params",
                            value = when {
                                result.model_n_params >= 1_000_000_000 ->
                                    String.format(Locale.ROOT, "%.1fB", result.model_n_params / 1_000_000_000f)
                                else ->
                                    String.format(Locale.ROOT, "%.0fM", result.model_n_params / 1_000_000f)
                            },
                            color = Color.Gray
                        )

                        MetricPill(
                            icon = R.drawable.memory,
                            label = "Size",
                            value = when {
                                result.model_size >= 1_073_741_824 ->
                                    String.format(Locale.ROOT, "%.1f GB", result.model_size / (1024f * 1024f * 1024f))
                                else ->
                                    String.format(Locale.ROOT, "%.0f MB", result.model_size / (1024f * 1024f))
                            },
                            color = Color.Gray
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun MetricPill(
    icon: Int,
    label: String,
    value: String,
    color: Color
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = 0.1f))
            .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(icon),
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(14.dp)
        )

        Column(
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                value,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = colorResource(R.color.secondaryColor)
            )
            Text(
                label,
                fontSize = 10.sp,
                color = Color.Gray
            )
        }
    }
}

@Preview
@Composable
fun BenchmarkScreenPreview() {
    BenchmarkScreen(
        modelManager = ModelManager()
    )
}