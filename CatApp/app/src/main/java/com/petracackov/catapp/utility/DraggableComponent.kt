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
import com.petracackov.catapp.utility.CardState.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun DraggableComponent(state: MutableState<CardState>, onDragEnd: () -> Unit, content: @Composable () -> Unit) {
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
                        snap(
                            coroutineScope = coroutineScope,
                            alpha = alpha,
                            xAxis = xAxis,
                            yAxis = yAxis,
                            rotation = rotation,
                            state = state.value,
                            screenWidth = screenWidth
                        )
                        onDragEnd()
                        state.value = MIDDLE
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
                 screenWidth: Float) {

    coroutineScope.launch {
        var xOffset: Float
        var alphaValue: Float

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
                    durationMillis = 300,
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
                        durationMillis = 300,
                        delayMillis = 0
                    )
                )
            }
        }

        launch {
            alpha.animateTo(
                targetValue = alphaValue,
                animationSpec = tween(
                    durationMillis = 300,
                    delayMillis = 0
                )
            )
            alpha.animateTo(
                targetValue = 1f,
                animationSpec = tween(
                    durationMillis = 300,
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

