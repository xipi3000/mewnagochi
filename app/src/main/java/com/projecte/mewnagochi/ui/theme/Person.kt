package com.projecte.mewnagochi.ui.theme

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
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
import kotlin.math.roundToInt

class Person {
    private lateinit var walkingAnimation : Animation
    private lateinit var idleAnimation : Animation
    private lateinit var jumpAnimation : Animation
    @Composable
    fun getWalkingMaps() : Array<ImageBitmap>{
        return arrayOf(
            ImageBitmap.imageResource(id = R.drawable.run0),
            ImageBitmap.imageResource(id = R.drawable.run1),
            ImageBitmap.imageResource(id = R.drawable.run2),
            ImageBitmap.imageResource(id = R.drawable.run3),
            ImageBitmap.imageResource(id = R.drawable.run4)
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
    fun buildSprite(){

        walkingAnimation = Animation(getWalkingMaps(), animTime = 1)
        idleAnimation = Animation(getIdleMaps(),animTime = 1)
        jumpAnimation = Animation(getJumpMaps(), animTime = 1)

    }

    @Composable
    fun Draw(){

        buildSprite()
        val aniManager = AnimationManager(arrayOf( idleAnimation,walkingAnimation,jumpAnimation))
        var offsetX by remember { mutableStateOf(0f) }
        var offsetY by remember { mutableStateOf(0f) }
        aniManager.playAnim(1)
        aniManager.Draw(modifier = Modifier.offset { IntOffset(offsetX.roundToInt(), offsetY.roundToInt()) }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = {
                        aniManager.playAnim(2)
                    },
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

            })

        val handler = Handler(Looper.getMainLooper())

        handler.post(object : Runnable {
            override fun run() {
                // Call your function here
                aniManager.update()

                // Schedule the next execution
                handler.postDelayed(this, 150) // Execute every 1000 milliseconds (1 second)
            }
        })

    }
}