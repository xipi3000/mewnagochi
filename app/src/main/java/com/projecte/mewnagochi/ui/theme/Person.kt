package com.projecte.mewnagochi.ui.theme

import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.IntOffset

import com.projecte.mewnagochi.R
import com.projecte.mewnagochi.ui.animation.Animation
import com.projecte.mewnagochi.ui.animation.AnimationManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.lang.Thread.sleep
import kotlin.math.roundToInt

class Person {
    private val mainScope = CoroutineScope(Dispatchers.Main)

    private lateinit var walkingAnimation : Animation
    private lateinit var idleAnimation : Animation
    private lateinit var jumpAnimation : Animation
    @Composable
    fun getWalkingMapsR() : Array<ImageBitmap>{
        return arrayOf(
            ImageBitmap.imageResource(id = R.drawable.run0),
            ImageBitmap.imageResource(id = R.drawable.run1),
            ImageBitmap.imageResource(id = R.drawable.run2),
            ImageBitmap.imageResource(id = R.drawable.run3),
            ImageBitmap.imageResource(id = R.drawable.run4)
        )
    }
    fun flipImageBitmap(imageBitmap: ImageBitmap): ImageBitmap {
        // Convert ImageBitmap to Bitmap
        val bitmap = imageBitmap.asAndroidBitmap()

        // Flip the Bitmap
        val matrix = Matrix()
        matrix.preScale(-1f, 1f) // Flip horizontally
        val flippedBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)

        // Convert back to ImageBitmap
        return flippedBitmap.asImageBitmap()
    }
    @Composable
    fun getWalkingMapsL() : Array<ImageBitmap>{
        return arrayOf(
            flipImageBitmap(ImageBitmap.imageResource(id = R.drawable.run0)),
            flipImageBitmap(ImageBitmap.imageResource(id = R.drawable.run1)),
                flipImageBitmap(ImageBitmap.imageResource(id = R.drawable.run2)),
                    flipImageBitmap(ImageBitmap.imageResource(id = R.drawable.run3)),
                        flipImageBitmap(ImageBitmap.imageResource(id = R.drawable.run4))
        )
    }
    @Composable
    fun getFallMaps() : Array<ImageBitmap>{
        return arrayOf(
            ImageBitmap.imageResource(id = R.drawable.fall1),
            ImageBitmap.imageResource(id = R.drawable.fall2),
            ImageBitmap.imageResource(id = R.drawable.fall3),
        )
    }
    @Composable
    fun getJumpMaps() : Array<ImageBitmap>{
        return arrayOf(
            ImageBitmap.imageResource(id = R.drawable.jump1),
            ImageBitmap.imageResource(id = R.drawable.jump2),
        )
    }

    @Composable
    fun getIdleMaps() : Array<ImageBitmap>{
        return  arrayOf(
            ImageBitmap.imageResource(id= R.drawable.advnt_full)
        )
    }



    @Composable
    fun buildSprite() : Array<Animation>{

    return arrayOf(
        Animation(getIdleMaps(),animTime = 1F),
        Animation(getWalkingMapsL(), animTime =8F),
        Animation(getJumpMaps(), animTime = 2F),
        Animation(getFallMaps(), animTime = 3F,freezLastFrame = true),
        Animation(getWalkingMapsR(), animTime =8F),
        )

    }
    var offsetX = mutableFloatStateOf(0F)
    fun returnToCenter(offsetY: Float){
        //TODO: FER QUE QUAN ESTA DRAGGING PARI DE MOURE'S
        val scope = CoroutineScope(Dispatchers.Main)
        if(offsetX.floatValue != 0F && offsetY==0F){

            Log.i("offset",offsetX.floatValue.toString())
            if(offsetX.floatValue > 0){
                aniManager.playAnim(1)
                scope.launch {
                    while (offsetX.floatValue >= 10F) {
                        offsetX.floatValue -= 1F
                        delay(10)
                    }
                    offsetX.floatValue = 0F
                    aniManager.playAnim(0)
                }
            }
            else{
                aniManager.playAnim(4)
                scope.launch {
                    while (offsetX.value <= 10F) {
                        offsetX.value += 1F
                        delay(10)
                    }
                    offsetX.value = 0F
                    aniManager.playAnim(0)
                }
            }
        }
    }

    var playing = mutableStateOf(true)
    lateinit var  aniManager: AnimationManager
    @Composable
    fun Draw(){

        aniManager = AnimationManager(buildSprite())
        var dragging by remember {
            mutableStateOf(true)
        }
        //var offsetX by remember { mutableStateOf(0f) }
        var offsetY by remember { mutableStateOf(0f) }
        Log.i("dragg","regeen")
        aniManager.playAnim(0)
        aniManager.Draw(modifier = Modifier
            .offset {
                IntOffset(
                    offsetX.value.roundToInt(),
                    if (offsetY.roundToInt() <= 0) offsetY.roundToInt() else 0
                )
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = {
                        dragging=true
                        aniManager.playAnim(2)
                    },
                    onDragEnd = {
                        dragging=false
                        Log.i("dragg", "stopped")
                        aniManager.playAnim(2)
                        val scope = CoroutineScope(Dispatchers.Main)
                        scope.launch {
                            var reps = 0F
                            // Loop until offsetY < 0F
                            while (offsetY <= 10F) {
                                // Your loop code here
                                // For example, update offsetY
                                reps++
                                offsetY += 10F + reps// Example decrement of offsetY
                                offsetX.value += 1F

                                // Delay for 1 second before the next iteration
                                delay(10)
                            }
                            offsetY = 0F

                            aniManager.playAnim(3)

                            delay(2000)

                            //TODO: FICAR ALGUN RANDOM DE TIPO PUGUI AIXECAR-SE MOLT RAPID O LENT
                            //TODO: FALTA ANIMACIÓ DE AIXECAR-SE


                            returnToCenter(offsetY)

                            //if (!dragging) aniManager.playAnim(0)
                            // Loop finished
                            // You can perform any cleanup or finalization here

                        }
                    },
                ) { change, dragAmount ->
                    change.consume()

                    offsetX.value += dragAmount.x
                    offsetY += dragAmount.y


                }

            })
        val handler = Handler(Looper.getMainLooper())
        handler.post(object : Runnable {
            override fun run() {

                if(playing.value)
                    aniManager.update()
                handler.postDelayed(this, 50) // Execute every 1000 milliseconds (1 second)
            }
        })


    }



}