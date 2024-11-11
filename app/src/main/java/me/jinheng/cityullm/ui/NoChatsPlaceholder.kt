package me.jinheng.cityullm.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import me.jinheng.cityullm.R

@Composable
fun NoChatsPlaceholder(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Seems to be you didn’t\n" + "start any chat yet",
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp
        )
        Icon(
            painter = painterResource(R.drawable.gridicons_chat),
            contentDescription = "",
            tint = colorResource(R.color.mainColor),
            modifier = Modifier
                .size(82.dp)
        )
    }
}