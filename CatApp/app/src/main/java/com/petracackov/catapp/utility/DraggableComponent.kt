package com.petracackov.catapp.utility

import androidx.compose.animation.Animatable
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animate
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun DraggableComponent(onDragEnd: () -> Unit, content: @Composable () -> Unit) {
    //val offset = remember { mutableStateOf(IntOffset.Zero) }
    val xAxis = remember { Animatable(0f) }
    val yAxis = remember { Animatable(0f) }
    val coroutineScope = rememberCoroutineScope()
    Box(
        content = { content() },
        modifier = Modifier
            .offset {
                IntOffset(xAxis.value.roundToInt(), yAxis.value.roundToInt())
            }
            .pointerInput(Unit) {
                    detectDragGestures(
                        onDragCancel = onDragEnd,
                        onDragEnd = {
                            coroutineScope.launch {
                                launch {
                                    xAxis.animateTo(0f)

                                }
                                launch {
                                    yAxis.animateTo(0f)
                                }
                            }
                        },
                        onDrag = { change, dragAmount ->
                            change.consume()
                            val deltaOffset =
                                IntOffset(dragAmount.x.roundToInt(), dragAmount.y.roundToInt())
                            coroutineScope.launch {
                                xAxis.snapTo(xAxis.value.plus(deltaOffset.x))
                                yAxis.snapTo(yAxis.value.plus(deltaOffset.y))
                            }
                        }
                    )
            }
    )

}