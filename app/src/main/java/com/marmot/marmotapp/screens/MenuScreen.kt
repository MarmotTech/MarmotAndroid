package com.marmot.marmotapp.screens

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.marmot.marmotapp.BenchmarkActivity
import com.marmot.marmotapp.R
import com.marmot.marmotapp.ModelsActivity
import com.marmot.marmotapp.ui.MenuCard

@Composable
fun MenuScreen() {
    val context = LocalContext.current

    val annotatedString = buildAnnotatedString {
        append("Dear, thanks for supporting \n" + "app in beta testing",)
        appendInlineContent(id = "huggingFace")
    }
    val inlineContentMap = mapOf(
        "huggingFace" to InlineTextContent(
            Placeholder(26.sp, 26.sp, PlaceholderVerticalAlign.TextBottom)
        ) {
            Image(
                painter = painterResource(R.drawable.hf_logo),
                modifier = Modifier.fillMaxSize(),
                contentDescription = ""
            )
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
            .safeDrawingPadding()
    ) {
        Box(
            modifier = Modifier
                .padding(bottom = 111.dp)
                .height(54.dp)
        )

        Text(
            annotatedString,
            modifier = Modifier
                .padding(bottom = 52.dp),
            inlineContent = inlineContentMap,
            fontSize = 24.sp,
            fontWeight = FontWeight.SemiBold
        )

        Column(
            modifier = Modifier
                .weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            MenuCard(
                title = "Start Message With any popular LLM you want",
                description = "Ask anything and LLM will be ready to proceed locally and answer you",
                buttonText = "Start Chatting",
                containerColor = colorResource(R.color.mainColor),
                onStartClick = {
                    context.startActivity(
                        Intent(context, ModelsActivity::class.java)
                    )
                },
                buttonColors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(R.color.secondaryColor),
                    contentColor = Color.White
                )
            )

            MenuCard(
                title = "Initiate Benchmarking of\n" + "our local models",
                description = "Help us collect statistics for different devices",
                buttonText = "Start Benchmarking",
                containerColor = colorResource(R.color.secondaryColor),
                onStartClick = {
                    context.startActivity(
                        Intent(context, BenchmarkActivity::class.java)
                    )
                },
                buttonColors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(R.color.mainColor),
                    contentColor = Color.White
                )
            )
        }
    }
}

@Preview
@Composable
fun MenuScreenPreview() {
    MenuScreen()
}
