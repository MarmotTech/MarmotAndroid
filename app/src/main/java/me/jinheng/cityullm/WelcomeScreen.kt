package me.jinheng.cityullm

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import de.apuri.physicslayout.lib.PhysicsLayout
import de.apuri.physicslayout.lib.physicsBody
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.paint
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.core.content.getSystemService
import de.apuri.physicslayout.lib.drag.DragConfig
import de.apuri.physicslayout.lib.simulation.Clock
import de.apuri.physicslayout.lib.simulation.rememberClock
import de.apuri.physicslayout.lib.simulation.rememberSimulation
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun WelcomeScreen() {
    val headerPainter = painterResource(R.drawable.globe_background)

    Column(
        modifier = Modifier.fillMaxHeight()
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

            Globe()
        }

        Column(
            modifier = Modifier
                .padding(horizontal = 47.dp, vertical = 37.dp)
        ) {
            Text(
                text = "Chat with AI, \n" +
                        "Privacy-First solution",
                fontSize = 32.sp,
                textAlign = TextAlign.Center
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
                Card(
                    shape = RoundedCornerShape(41.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = colorResource(R.color.mainColor),
                    ),
                    modifier = Modifier
                        .height(82.dp)
                        .fillMaxWidth()
                ) {}
            }
        }
    }
}

@Composable
fun Globe() {
    var gravity by remember { mutableStateOf(Offset.Zero) }
    val clock = rememberClock()
    var ballCounter by remember { mutableStateOf(0) }
    val composableScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        composableScope.launch {
            for (i in 1..50) {
                ballCounter += 1
                delay(50)
            }
        }
    }
    LockScreenOrientation()
    GravitySensor { (x, y) ->
        gravity = Offset(-x, y).times(5f)
    }
    PhysicInstance(
        gravityProvider = { gravity },
        clock = clock,
        ballCounter = ballCounter
    )
}

@Composable
fun PhysicInstance(
    gravityProvider: () -> Offset,
    clock: Clock,
    ballCounter: Int
) {
    val simulation = rememberSimulation(clock)
    simulation.setGravity(gravityProvider())

    PhysicsLayout(
        modifier = Modifier
            .fillMaxSize(),
        simulation = simulation,
    ) {
        (1..ballCounter).forEach {
            key(it) {
                Ball(
                    id = it.toString(),
                )
            }
        }
    }
}

@Composable
fun BoxScope.Ball(
    id: String,
) {
    Box(
        Modifier
            .align(Alignment.TopCenter)
    ) {
        Box(
            modifier = Modifier
                .physicsBody(
                    id = id,
                    shape = CircleShape,
                    dragConfig = DragConfig()
                )
                .size(40.dp),
        ) {
            Image(
                painter = painterResource(id = R.drawable.hf_logo),
                contentDescription = null,
                modifier = Modifier
                    .wrapContentSize(unbounded = true)
                    .size(68.dp)
            )
//            Icon(
//                modifier = Modifier
//                    .size(48.dp)
//                    .padding(4.dp),
//                imageVector = Icons.Default.Star,
//                contentDescription = "",
//                tint = Color.White
//            )
        }
    }
}

@Composable
fun GravitySensor(
    onGravityChanged: (List<Float>) -> Unit
) {
    val context = LocalContext.current
    DisposableEffect(Unit) {
        val sensorManager = context.getSystemService<SensorManager>()!!

        val gravitySensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY)

        val gravityListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                val (x, y, z) = event.values
                onGravityChanged(listOf(x,y,z))
            }

            override fun onAccuracyChanged(p0: Sensor?, p1: Int) {

            }
        }

        sensorManager.registerListener(
            gravityListener,
            gravitySensor,
            SensorManager.SENSOR_DELAY_NORMAL,
            SensorManager.SENSOR_DELAY_NORMAL
        )

        onDispose {
            sensorManager.unregisterListener(gravityListener)
        }
    }
}

@Composable
fun LockScreenOrientation() {
    val context = LocalContext.current
    DisposableEffect(context) {
        context.requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LOCKED
        onDispose {
            context.requireActivity().requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
    }
}


fun Context.requireActivity(): Activity {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    throw IllegalStateException("No activity was present but it is required.")
}

