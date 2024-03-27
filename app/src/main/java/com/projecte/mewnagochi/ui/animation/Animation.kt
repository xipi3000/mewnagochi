package com.projecte.mewnagochi.ui.animation

import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize


class Animation(
    private val frames :  Array<ImageBitmap>,
    private val frameIndex :MutableState<Int> = mutableIntStateOf(0),
    private val animTime : Int,
    private var frameTime:Float = (animTime/frames.size).toFloat(),
    private var lastFrame: Long = System.currentTimeMillis()
) {

    var isPlaying : Boolean = false


    fun play(){
        isPlaying=true
    }
    fun stop(){
        isPlaying=false
    }


    @Composable
    fun Draw(modifier: Modifier) {

        if (!isPlaying) return




        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceAround) {
            Text(text = frameIndex.value.toString())
            val handler = Handler(Looper.getMainLooper())
            Canvas(

                modifier = modifier
                    .size(Dp(100F))

                    .background(Color.Red)


            ) {

                    drawImage(frames[frameIndex.value],
                        //srcSize = IntSize(frames[frameIndex.value].width*1, frames[frameIndex.value].height*1),
                        dstSize =  IntSize(frames[frameIndex.value].width/2, frames[frameIndex.value].height/2),

                        )
            }
        }
    }


    fun update() {

        if (!isPlaying) return
        if (System.currentTimeMillis() - lastFrame > frameTime * 1000) {
            //Log.i("animating", frames[frameIndex.value].toString())
            frameIndex.value++
            frameIndex.value = if (frameIndex.value >= frames.size) 0 else frameIndex.value
            lastFrame = System.currentTimeMillis()
        }
    }


}