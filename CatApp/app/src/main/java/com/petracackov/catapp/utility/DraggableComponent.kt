package com.petracackov.catapp.utility

import android.content.res.Resources
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.petracackov.catapp.utility.CardState.*
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun DraggableComponent(onDragEnd: () -> Unit, state: MutableState<CardState>, content: @Composable () -> Unit) {
    val xAxis = remember { Animatable(0f) }
    val yAxis = remember { Animatable(0f) }
    val rotation = remember { Animatable(0f) }
    val alpha = remember { Animatable(1f) }
    val coroutineScope = rememberCoroutineScope()
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp * Resources.getSystem().displayMetrics.density

    Box(
        content = {
            Column() {
                Text(text = state.value.name)
                content()
            } },
        modifier = Modifier
            .alpha(alpha.value)
            .offset {
                IntOffset(xAxis.value.roundToInt(), yAxis.value.roundToInt())
            }
            .rotate(degrees = rotation.value)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragCancel = onDragEnd,
                    onDragEnd = {
                        coroutineScope.launch {
                            when (state.value) {
                                LEFT -> {
                                    launch {
                                        xAxis.animateTo(
                                            targetValue = (-screenWidth - 50),
                                            animationSpec = tween(
                                                durationMillis = 300,
                                                delayMillis = 0
                                            )
                                        )
                                        rotation.snapTo(0f)
                                        xAxis.snapTo(0f)
                                        yAxis.snapTo(0f)
                                    }

                                    launch {
                                        alpha.animateTo(
                                            targetValue = 0f,
                                            animationSpec = tween(
                                                durationMillis = 300,
                                                delayMillis = 0
                                        ))
                                        alpha.animateTo(
                                            targetValue = 1f,
                                            animationSpec = tween(
                                                durationMillis = 50,
                                                delayMillis = 0
                                            ))
                                    }

                                }
                                RIGHT -> {
                                    launch {
                                        xAxis.animateTo(
                                            targetValue = (screenWidth + 50),
                                            animationSpec = tween(
                                                durationMillis = 300,
                                                delayMillis = 0
                                            )
                                        )
                                        rotation.snapTo(0f)
                                        xAxis.snapTo(0f)
                                        yAxis.snapTo(0f)
                                    }

                                    launch {
                                        alpha.animateTo(
                                            targetValue = 0f,
                                            animationSpec = tween(
                                                durationMillis = 300,
                                                delayMillis = 0
                                            ))
                                        alpha.animateTo(
                                            targetValue = 1f,
                                            animationSpec = tween(
                                                durationMillis = 50,
                                                delayMillis = 0
                                            ))
                                    }

                                }

                                MIDDLE -> {
                                    launch {
                                        xAxis.animateTo(0f)
                                    }
                                    launch {
                                        yAxis.animateTo(0f)
                                    }
                                    launch {
                                        rotation.animateTo(0f)
                                    }
                                }
                            }
                            state.value = MIDDLE
                        }
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        coroutineScope.launch {
                            val deltaOffset =
                                IntOffset(dragAmount.x.roundToInt(), dragAmount.y.roundToInt())
                            val newX = xAxis.value.plus(deltaOffset.x)
                            val newY = yAxis.value.plus(deltaOffset.y)

                            println(state)
                            xAxis.snapTo(newX)
                            yAxis.snapTo(newY)

                            if (newX > 150) {
                                state.value = RIGHT
                                rotation.animateTo(10f)
                            } else if (newX < -150) {
                                state.value = LEFT
                                rotation.animateTo(-10f)
                            } else {
                                state.value = MIDDLE
                                rotation.animateTo(0f)
                            }
                        }
                    }
                )
            }
    )
}

enum class CardState {
    LEFT,
    RIGHT,
    MIDDLE
}

