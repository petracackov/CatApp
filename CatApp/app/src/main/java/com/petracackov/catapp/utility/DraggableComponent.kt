package com.petracackov.catapp.utility

import android.annotation.SuppressLint
import android.content.res.Resources
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toDuration

@Composable
fun DraggableComponent(state: MutableState<CardState>, onTransitionAnimationEnd: () -> Unit, onVisibilityAnimationEnd: () -> Unit, content: @Composable () -> Unit) {
    val xAxis = remember { Animatable(0f) }
    val yAxis = remember { Animatable(0f) }
    val rotation = remember { Animatable(0f) }
    val alpha = remember { Animatable(1f) }
    val coroutineScope = rememberCoroutineScope()
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp * Resources.getSystem().displayMetrics.density
    val animationDuration: Int = 300

    Box(
        content = {
            content()
        },
        modifier = Modifier
            .alpha(alpha.value)
            .offset(x = xAxis.value.dp, y = yAxis.value.dp)
            .rotate(degrees = rotation.value)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragCancel = {
                        snap(
                            coroutineScope = coroutineScope,
                            alpha = alpha,
                            xAxis = xAxis,
                            yAxis = yAxis,
                            rotation = rotation,
                            state = state.value,
                            screenWidth = screenWidth,
                            animationDuration = animationDuration
                        )
                    },
                    onDragEnd = {
                        snap(
                            coroutineScope = coroutineScope,
                            alpha = alpha,
                            xAxis = xAxis,
                            yAxis = yAxis,
                            rotation = rotation,
                            state = state.value,
                            screenWidth = screenWidth,
                            animationDuration = animationDuration
                        )
                        coroutineScope.launch {
                            delay(animationDuration.toLong())
                            onTransitionAnimationEnd()
                            delay(animationDuration.toLong())
                            onVisibilityAnimationEnd()
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

private fun snap(coroutineScope: CoroutineScope,
                 alpha:  Animatable<Float, AnimationVector1D>,
                 xAxis: Animatable<Float, AnimationVector1D>,
                 yAxis: Animatable<Float, AnimationVector1D>,
                 rotation: Animatable<Float, AnimationVector1D>,
                 state: CardState,
                 screenWidth: Float,
                 animationDuration: Int) {

    coroutineScope.launch {
        val xOffset: Float
        val alphaValue: Float

        when (state) {
            LEFT -> {
                xOffset = -screenWidth - 50f
                alphaValue = 0f
            }
            RIGHT -> {
                xOffset = screenWidth + 50f
                alphaValue = 0f
            }
            MIDDLE -> {
                xOffset = 0f
                alphaValue = 1f
            }
        }

        launch {
            xAxis.animateTo(
                targetValue = (xOffset),
                animationSpec = tween(
                    durationMillis = animationDuration,
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
                        durationMillis = animationDuration,
                        delayMillis = 0
                    )
                )
            }
        }

        launch {
            alpha.animateTo(
                targetValue = alphaValue,
                animationSpec = tween(
                    durationMillis = animationDuration,
                    delayMillis = 0
                )
            )
            alpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = animationDuration,
                    delayMillis = 0
                )
            )
        }
    }
}


enum class CardState {
    LEFT,
    RIGHT,
    MIDDLE
}

