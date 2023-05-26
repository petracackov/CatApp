package com.petracackov.catapp.utility

import android.content.res.Resources
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.IntOffset
import com.petracackov.catapp.utility.CardState.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

// MARK: -  Constants

@Composable
fun DraggableComponent(state: MutableState<CardState>, isHidden: MutableState<Boolean>, transitionDuration: Int, onTransitionAnimationEnd: () -> Unit, content: @Composable () -> Unit) {
    val xAxis = remember { Animatable(0f) }
    val yAxis = remember { Animatable(0f) }
    val rotation = remember { Animatable(0f) }
    val alpha = remember { Animatable(1f) }
    val coroutineScope = rememberCoroutineScope()
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp * Resources.getSystem().displayMetrics.density

    LaunchedEffect(key1 = isHidden.value) {
        if (isHidden.value) {
            alpha.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = transitionDuration)
            )
        } else {
            alpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = transitionDuration)
            )
        }
    }

    Box(
        content = {
            content()
        },
        modifier = Modifier
            .alpha(alpha.value)
            .offset {
                IntOffset(xAxis.value.roundToInt(), yAxis.value.roundToInt())
            }
            .rotate(degrees = rotation.value)
            .pointerInput(Unit) {
                    detectDragGestures(
                        onDragCancel = {
                            if (!isHidden.value) {
                                snap(
                                    coroutineScope = coroutineScope,
                                    xAxis = xAxis,
                                    yAxis = yAxis,
                                    rotation = rotation,
                                    state = state.value,
                                    screenWidth = screenWidth,
                                    transitionDuration = transitionDuration
                                )
                            }
                        },
                        onDragEnd = {
                            if (!isHidden.value) {
                                snap(
                                    coroutineScope = coroutineScope,
                                    xAxis = xAxis,
                                    yAxis = yAxis,
                                    rotation = rotation,
                                    state = state.value,
                                    screenWidth = screenWidth,
                                    transitionDuration = transitionDuration
                                )

                                isHidden.value = (state.value != MIDDLE)

                                coroutineScope.launch {
                                    delay(transitionDuration.toLong())
                                    onTransitionAnimationEnd()
                                    state.value = MIDDLE
                                }
                            }
                        },
                        onDrag = { change, dragAmount ->
                            if (!isHidden.value) {
                                change.consume()
                                coroutineScope.launch {
                                    val deltaOffset =
                                        IntOffset(
                                            dragAmount.x.roundToInt(),
                                            dragAmount.y.roundToInt()
                                        )
                                    val newX = xAxis.value.plus(deltaOffset.x)
                                    val newY = yAxis.value.plus(deltaOffset.y)

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
                        }
                    )
            }
    )
}

private fun snap(coroutineScope: CoroutineScope,
                 xAxis: Animatable<Float, AnimationVector1D>,
                 yAxis: Animatable<Float, AnimationVector1D>,
                 rotation: Animatable<Float, AnimationVector1D>,
                 state: CardState,
                 screenWidth: Float,
                 transitionDuration: Int) {

    coroutineScope.launch {
        val xOffset: Float = when (state) {
            LEFT -> {
                -screenWidth - 50f
            }
            RIGHT -> {
                screenWidth + 50f
            }
            MIDDLE -> {
                0f
            }
        }

        launch {
            xAxis.animateTo(
                targetValue = (xOffset),
                animationSpec = tween(
                    durationMillis = transitionDuration,
                    delayMillis = 0
                )
            )
            rotation.snapTo(0f)
            xAxis.snapTo(0f)
            yAxis.snapTo(0f)
        }

        if (state == MIDDLE) {
            launch {
                yAxis.animateTo(
                    targetValue = 0f,
                    animationSpec = tween(
                        durationMillis = transitionDuration,
                        delayMillis = 0
                    )
                )
            }
        }
    }
}


enum class CardState {
    LEFT,
    RIGHT,
    MIDDLE
}

