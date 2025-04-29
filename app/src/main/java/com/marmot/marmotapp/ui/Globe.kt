package com.marmot.marmotapp.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import de.apuri.physicslayout.lib.PhysicsLayout
import de.apuri.physicslayout.lib.drag.DragConfig
import de.apuri.physicslayout.lib.physicsBody
import de.apuri.physicslayout.lib.simulation.Clock
import de.apuri.physicslayout.lib.simulation.rememberClock
import de.apuri.physicslayout.lib.simulation.rememberSimulation
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.marmot.marmotapp.R

@Composable
fun Globe() {
    var gravity by remember { mutableStateOf(Offset.Zero) }
    val clock = rememberClock()
    var ballCounter by remember { mutableIntStateOf(0) }
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
        }
    }
}
