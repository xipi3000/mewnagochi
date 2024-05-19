package com.projecte.mewnagochi.ui.animation

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.projecte.mewnagochi.screens.home.HomeScreenViewModel
import com.projecte.mewnagochi.services.storage.UserPreferences


class Animation(
    private val frames: Array<ImageBitmap>,
    private val frameIndex: MutableState<Int> = mutableIntStateOf(0),
    private val animTime: Float,
    private val frameTime: Float = animTime / frames.size,
    private var lastFrame: MutableState<Long> = mutableLongStateOf(System.currentTimeMillis()),
    private var freezLastFrame: Boolean = false,
    private val dialog: ImageBitmap
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
    fun Draw(
        modifier: Modifier,
        viewModel: HomeScreenViewModel = viewModel()
    ) {
        val userPreferences by viewModel.userPreferences.collectAsState(UserPreferences())
        if (!isPlaying) return
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {
            if (userPreferences?.notificationText?.isNotEmpty()?:false) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = modifier
                        .offset(
                            y=-30.dp
                        )
                ) {



                    Canvas(modifier = Modifier
                        .width(dialog.width.dp * 10 / 26)
                        .height(dialog.height.dp * 10 / 31)) {
                        drawImage(
                            dialog,
                        )
                    }
                    Text(text = userPreferences?.notificationText?:"",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.run {
                            width(dialog.width.dp * 10 / 30)
                                                .height(dialog.height.dp * 10 / 37)
                        }

                    )

                }
            }
            Canvas(
                modifier = modifier
                    .width(frames[frameIndex.value].width.dp / 5)
                    .height(frames[frameIndex.value].height.dp * 10 / 53)
                //.clipToBounds()
            ) {
                //drawImage(dialog, topLeft = Offset(-dialog.width.toFloat()+size.width,-frames[frameIndex.value].height.toFloat() * 10 / 30))

                drawImage(
                    frames[frameIndex.value],
                    dstSize = IntSize(size.width.toInt(), size.height.toInt()),
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