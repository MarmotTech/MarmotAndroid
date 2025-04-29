package com.marmot.marmotapp.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetDefaults
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import com.marmot.marmotapp.R
import com.marmot.marmotapp.models.ModelManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DownloadBottomSheet(
    modelManager: ModelManager,
    onComplete: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = { it != SheetValue.Hidden },
    )
    val isDownloading by modelManager.isDownloading.collectAsState(false)
    val downloadProgress by modelManager.downloadProgress.collectAsState()

    ModalBottomSheet(
        onDismissRequest = {},
        sheetState = sheetState,
        containerColor = Color.White,
        dragHandle = {},
        properties = ModalBottomSheetDefaults.properties(
            shouldDismissOnBackPress = false
        ),
        windowInsets = BottomSheetDefaults.windowInsets.only(
            WindowInsetsSides.Bottom
        ),
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Download Model",
                modifier = Modifier
                    .padding(bottom = 8.dp),
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                "Could not find model in local, download tinyllama-1.1b-chat-v1.0 and have a try",
                modifier = Modifier
                    .padding(
                        bottom = 32.dp,
                        start = 8.dp,
                        end = 8.dp
                    ),
                textAlign = TextAlign.Center,
                fontSize = 12.sp,
            )

            if (isDownloading) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth(),
                    progress = {
                        downloadProgress.toFloat() / 100f
                    },
                    color = colorResource(R.color.mainColor)
                )
            } else {
                Button(
                    modifier = Modifier
                        .height(54.dp)
                        .fillMaxWidth(),
                    onClick = {
                        coroutineScope.launch {
                            modelManager.downloadModels(
                                modelManager.missingModels().subList(0, 2)
                            )
                            onComplete()
                        }
                    },
                    shape = RoundedCornerShape(24.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(R.color.mainColor),
                    )
                ) {
                    Text(
                        "Download"
                    )
                }
            }
        }
    }
}

@Preview
@Composable
fun DownloadBottomSheetPreview() {
    DownloadBottomSheet(
        modelManager = ModelManager(),
        onComplete = {}
    )
}
