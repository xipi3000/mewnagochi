package com.projecte.mewnagochi.ui.animation

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.res.imageResource
import com.projecte.mewnagochi.R

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
    fun Draw() {

        if (!isPlaying) return
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceAround) {
            Text(text = frameIndex.value.toString())
            androidx.compose.foundation.Canvas(
                modifier = Modifier

                    .background(Color.Red)
            ) {



                    drawImage(frames[frameIndex.value])
            }
        }


        //canvas.drawImage(frames[frameIndex], Offset.Zero,Paint())

    }


    fun update() {

        if (!isPlaying) return
        if (System.currentTimeMillis() - lastFrame > frameTime * 1000) {
            Log.i("animating", frames[frameIndex.value].toString())
            frameIndex.value++
            frameIndex.value = if (frameIndex.value >= frames.size) 0 else frameIndex.value
            lastFrame = System.currentTimeMillis()
        }
    }


}