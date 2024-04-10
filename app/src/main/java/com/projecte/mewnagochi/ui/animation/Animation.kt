package com.projecte.mewnagochi.ui.animation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize


class Animation(
    private val frames: Array<ImageBitmap>,
    private val frameIndex: MutableState<Int> = mutableIntStateOf(0),
    private val animTime: Float,
    private val frameTime: Float = animTime / frames.size,
    private var lastFrame: MutableState<Long> = mutableLongStateOf(System.currentTimeMillis()),
    private var freezLastFrame: Boolean = false
) {

    var isPlaying: Boolean = false


    fun play() {
        isPlaying = true
        frameIndex.value = 0
    }

    fun stop() {
        isPlaying = false
    }

    @Composable
    fun Draw(modifier: Modifier) {
        if (!isPlaying) return
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {
            Canvas(
                modifier = modifier
                    .width(Dp(100F))
                    .height(Dp(180f))
                    .background(Color.Red)
            ) {
                drawImage(
                    frames[frameIndex.value],
//                    dstSize = IntSize(
//                        frames[frameIndex.value].width / 2, frames[frameIndex.value].height / 2
//                    ),
                    //Canvas size * 3.5
                    dstSize = IntSize (frames[frameIndex.value].width / 2, 630)
                )
            }
        }
    }

    fun update() {
        if (!isPlaying) return
        if (!freezLastFrame || frameIndex.value != frames.size - 1) {
            if (System.currentTimeMillis() - lastFrame.value > frameTime * 100) {
                frameIndex.value++
                frameIndex.value = if (frameIndex.value >= frames.size) 0 else frameIndex.value
                lastFrame.value = System.currentTimeMillis()
            }
        }
    }
}