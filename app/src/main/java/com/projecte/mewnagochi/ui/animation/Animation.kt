package com.projecte.mewnagochi.ui.animation

import android.graphics.Bitmap
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.animateDecay
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.updateTransition
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
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import com.projecte.mewnagochi.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.Thread.sleep
import kotlin.math.roundToInt



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
    enum class BoxState {
        Collapsed,
        Expanded
    }

    @Composable
    fun Draw() {

        if (!isPlaying) return

        var offsetX by remember { mutableStateOf(0f) }
        var offsetY by remember { mutableStateOf(0f) }
        var enabled by remember { mutableStateOf(true) }

        val alphaX: Float by animateFloatAsState(if (enabled) offsetX else 0f)
        val alphaY: Float by animateFloatAsState(if (enabled) offsetY else 0f)
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceAround) {
            Text(text = frameIndex.value.toString())
            val handler = Handler(Looper.getMainLooper())
            Canvas(

                modifier = Modifier
                    .size(Dp(100F))
                    .offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragEnd = {


                                val scope = CoroutineScope(Dispatchers.Main)

                                scope.launch {

                                    var reps = 0F
                                    // Loop until offsetY < 0F
                                    while (offsetY <= 0F) {
                                        // Your loop code here
                                        // For example, update offsetY
                                        reps++
                                        offsetY += 10F + reps// Example decrement of offsetY
                                        offsetX += 1F

                                        // Delay for 1 second before the next iteration
                                        delay(10)
                                    }

                                    // Loop finished
                                    // You can perform any cleanup or finalization here
                                }



                            },

                        ) { change, dragAmount ->
                            change.consume()
                            Log.i("offset",offsetY.toString())
                            offsetX += dragAmount.x
                            offsetY += dragAmount.y
                        }

                    }
                    .background(Color.Red)


            ) {

                    drawImage(frames[frameIndex.value],
                        //srcSize = IntSize(frames[frameIndex.value].width*1, frames[frameIndex.value].height*1),
                        dstSize =  IntSize(frames[frameIndex.value].width/2, frames[frameIndex.value].height/2),

                        )
            }
        }


        //canvas.drawImage(frames[frameIndex], Offset.Zero,Paint())

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