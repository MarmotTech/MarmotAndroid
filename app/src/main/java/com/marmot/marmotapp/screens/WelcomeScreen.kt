package com.marmot.marmotapp.screens

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.marmot.marmotapp.R
import com.marmot.marmotapp.ui.Globe
import com.marmot.marmotapp.ui.SwipeToStart
import com.marmot.marmotapp.MenuActivity
import com.marmot.marmotapp.utils.requireActivity

@Composable
fun WelcomeScreen() {
    val headerPainter = painterResource(R.drawable.globe_background)
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Box(
            modifier = Modifier
                .weight(1f)
        ) {
            Image(
                painter = headerPainter,
                contentDescription = "",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .matchParentSize()
            )

            if (LocalInspectionMode.current) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                ) {  }
            } else {
                Globe()
            }

            Row(
                modifier = Modifier
                    .padding(40.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
                verticalAlignment = Alignment.CenterVertically,

            ) {
                Image(
                    painter = painterResource(id = R.drawable.gridicons_chat),
                    contentDescription = null,
                )

                Text(
                    "MARMOT",
                    fontSize = 20.sp,
                    color = Color.White
                )
            }
        }

        Column(
            modifier = Modifier
                .padding(horizontal = 42.dp, vertical = 37.dp)
        ) {
            Text(
                text = "Chat with AI, \n" +
                        "Privacy-First solution",
                fontSize = 32.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "A powerful language model running locally on your device, dare to try it?",
                fontSize = 16.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .padding(
                        top = 18.dp,
                        bottom = 33.dp,
                    )
            )
            Box(
                modifier = Modifier
                    .padding(
                        top = 50.dp,
                        bottom = 43.dp
                    )
                    .fillMaxWidth()
            ) {
                SwipeToStart(
                    onComplete = {
                        context.startActivity(
                            Intent(context, MenuActivity::class.java)
                        )
                        context.requireActivity().finish()
                    }
                )
            }
        }
    }
}

@Composable
@Preview
fun WelcomeScreenPreview() {
    WelcomeScreen()
}
