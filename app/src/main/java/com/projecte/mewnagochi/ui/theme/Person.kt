package com.projecte.mewnagochi.ui.theme

import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.projecte.mewnagochi.MyViewModel
import com.projecte.mewnagochi.PersonViewModel

import com.projecte.mewnagochi.R
import com.projecte.mewnagochi.ui.animation.Animation
import com.projecte.mewnagochi.ui.animation.AnimationManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.ViewModelProvider

//TODO: FER QUE ES MOGUI RANDOM
//TODO: FER QUE ES MOGUI SI S'ACLICA
//TODO: FICAR FONDO
class Person ( ) {

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
    fun getUpMaps() : Array<ImageBitmap>{
        return arrayOf(
            ImageBitmap.imageResource(id = R.drawable.up1),
            ImageBitmap.imageResource(id = R.drawable.up2),
            ImageBitmap.imageResource(id = R.drawable.up3),
            ImageBitmap.imageResource(id = R.drawable.up4),
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
            ImageBitmap.imageResource(id= R.drawable.idle)
        )
    }
    @Composable
    fun getClickedMaps() : Array<ImageBitmap>{
        return  arrayOf(
            ImageBitmap.imageResource(id= R.drawable.clicked)
        )
    }



    @Composable
    fun BuildSprite(){

        aniManager = AnimationManager(arrayOf(
        Animation(getIdleMaps(),animTime = 1F),
        Animation(getWalkingMapsL(), animTime =8F),
        Animation(getJumpMaps(), animTime = 2F),
        Animation(getFallMaps(), animTime = 3F,freezLastFrame = true),
        Animation(getWalkingMapsR(), animTime =8F),
        Animation(getUpMaps(), animTime =10F),
        Animation(getClickedMaps(), animTime =1F),
        ))

    }

    fun returnToCenter(

        offsetX: Float,
        offsetY: Float,
        personViewModel : PersonViewModel,){
        personState=PersonState.RET_TO_CENTER
        var staticOffsetx = offsetX
        val scope = CoroutineScope(Dispatchers.Main)
        if(staticOffsetx != 0F && offsetY==0F){

            Log.i("offset",staticOffsetx.toString())
            if(staticOffsetx > 0){
                aniManager.playAnim(1)
                scope.launch {
                    while (staticOffsetx >= 10F) {
                        if(personState!=PersonState.RET_TO_CENTER) return@launch
                        personViewModel.setOffsetX( staticOffsetx- 1F)
                        staticOffsetx -= 1F
                        delay(10)
                    }
                    personViewModel.setOffsetX(0F)
                    aniManager.playAnim(0)
                }
            }
            else{
                aniManager.playAnim(4)
                scope.launch {
                    while (staticOffsetx <= 10F) {
                        if(personState!=PersonState.RET_TO_CENTER) return@launch
                        personViewModel.setOffsetX( staticOffsetx +1F)
                        staticOffsetx += 1F
                        delay(10)

                    }
                    personViewModel.setOffsetX(0F)
                    aniManager.playAnim(0)
                }
            }
        }
    }

    var personState : PersonState = PersonState.IDLE

    lateinit var  aniManager: AnimationManager
    @Composable
    fun Draw(personViewModel : PersonViewModel = viewModel()
    ){

        val offsetX  by personViewModel.offsetX.collectAsState()

        var dragging by remember {
            mutableStateOf(true)
        }
        //var offsetX by remember { mutableStateOf(0f) }
        var offsetY by remember { mutableStateOf(0f) }
        Log.i("dragg","regeen")
        aniManager.playAnim(0)
        val interactionSource = remember { MutableInteractionSource() }
        aniManager.Draw(modifier = Modifier
            .clickable(
                interactionSource = interactionSource,
                indication = null
            ) {
                val scope = CoroutineScope(Dispatchers.Main)
                scope.launch {
                    aniManager.playAnim(6)
                    delay(100)
                    aniManager.playAnim(0)
                }
                 }
            .offset {
                IntOffset(
                    offsetX.roundToInt(),
                    if (offsetY.roundToInt() <= 0) offsetY.roundToInt() else 0
                )
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = {
                        personState=PersonState.BEING_DRAGED
                        dragging = true
                        aniManager.playAnim(2)
                    },
                    onDragEnd = {
                        dragging = false
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
                                personViewModel.setOffsetX(offsetX + 1F)

                                // Delay for 1 second before the next iteration
                                delay(10)
                            }
                            offsetY = 0F

                            aniManager.playAnim(3)

                            delay(2000)
                            aniManager.playAnim(5)
                            delay(700)
                            //TODO: FICAR ALGUN RANDOM DE TIPO PUGUI AIXECAR-SE MOLT RAPID O LENT
                            //TODO: FALTA ANIMACIÓ DE AIXECAR-SE


                            returnToCenter(offsetX = offsetX,offsetY =  offsetY, personViewModel)

                            //if (!dragging) aniManager.playAnim(0)
                            // Loop finished
                            // You can perform any cleanup or finalization here

                        }
                    },
                ) { change, dragAmount ->
                    change.consume()

                    personViewModel.setOffsetX(offsetX + dragAmount.x)
                    offsetY += dragAmount.y


                }

            })
        val handler = Handler(Looper.getMainLooper())
        handler.post(object : Runnable {
            override fun run() {
                aniManager.update()
                handler.postDelayed(this, 50) // Execute every 1000 milliseconds (1 second)
            }
        })


    }



}