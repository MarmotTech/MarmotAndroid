package me.jinheng.cityullm.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.jinheng.cityullm.R
import me.jinheng.cityullm.models.BenchmarkResultsDatabase
import me.jinheng.cityullm.models.BenchmarkResultsRepository
import me.jinheng.cityullm.ui.BenchmarkBottomSheet
import me.jinheng.cityullm.utils.ModelOperations
import java.util.Locale

@Composable
fun BenchmarkScreen() {
    var showCreateSheet by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val resultsRepository = remember {
        BenchmarkResultsRepository(
            BenchmarkResultsDatabase.getDatabase(context).benchmarkResultsDao()
        )
    }
    val benchmarkResults by resultsRepository.benchmarkResults.observeAsState(listOf())

    Box {
        Column(
            modifier = Modifier
                .background(Color.White)
                .padding(top = 24.dp)
                .safeDrawingPadding()
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    "MBZUAI On-Device LLM",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Button(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(R.color.secondaryColor)
                        ),
                        onClick = {

                        }
                    ) {
                        Text(
                            "Export",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                    Button(
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorResource(R.color.mainColor)
                        ),
                        onClick = {
                            showCreateSheet = true
                        }
                    ) {
                        Text(
                            "Create",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(colorResource(R.color.tertiaryColor)),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(benchmarkResults) { result ->
                    val modelInfo = ModelOperations.allSupportModels.first {
                        it.modelLocalPath == result.model_name
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(24.dp))
                            .background(Color.White)
                            .fillMaxWidth()
                            .padding(24.dp)
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                modelInfo.modelName,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                            )

                            LazyRow(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                items(result.task_results.entries.toList()) { (taskName, taskResult) ->
                                    ResultColumn(
                                        name = taskName,
                                        value = taskResult,
                                        format = "%.3f"
                                    )
                                }
                            }

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                ResultColumn(
                                    name = "Decode Throughput",
                                    value = result.decode_throughput
                                )
                                ResultColumn(
                                    name = "Prefill Throughput",
                                    value = result.prefill_throughput
                                )
                            }

                            ResultColumn(
                                name = "Model Params",
                                value = result.model_n_params.toFloat(),
                                format = "%.0f"
                            )

                            ResultColumn(
                                name = "Model Size",
                                value = result.model_size.toFloat(),
                                format = "%.0f"
                            )
                        }
                    }
                }
            }
        }

        if (showCreateSheet) {
            BenchmarkBottomSheet(
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
fun ResultColumn(
    name: String,
    value: Float,
    format: String = "%.3f t/s"
) {
    Column(
        modifier = Modifier
            .padding(
                vertical = 8.dp
            ),
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            String.format(Locale.ROOT, format, value),
            fontSize = 32.sp,
            style = LocalTextStyle.current.copy(fontFeatureSettings = "tnum"),
            color = colorResource(R.color.mainColor),
            fontWeight = FontWeight.ExtraBold,
        )
        Text(
            name,
            fontSize = 16.sp,
            color = colorResource(R.color.secondaryColor),
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Preview
@Composable
fun BenchmarkScreenPreview() {
    BenchmarkScreen()
}
