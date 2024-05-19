package com.projecte.mewnagochi.ui.theme

import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.projecte.mewnagochi.R
import com.projecte.mewnagochi.services.storage.UserPreferences
import com.projecte.mewnagochi.ui.animation.Animation
import com.projecte.mewnagochi.ui.animation.AnimationManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt


class PersonInvited() {

    @Composable
    fun getWalkingMapsR(): Array<ImageBitmap> {
        return arrayOf(
            ImageBitmap.imageResource(id = R.drawable.walk1_f),
            ImageBitmap.imageResource(id = R.drawable.walk2_f),
            ImageBitmap.imageResource(id = R.drawable.walk3_f),
            ImageBitmap.imageResource(id = R.drawable.walk4_f),
            ImageBitmap.imageResource(id = R.drawable.walk5_f),
            ImageBitmap.imageResource(id = R.drawable.walk6_f)
        )
    }

    fun flipImageBitmap(imageBitmap: ImageBitmap): ImageBitmap {
        // Convert ImageBitmap to Bitmap
        val bitmap = imageBitmap.asAndroidBitmap()

        // Flip the Bitmap
        val matrix = Matrix()
        matrix.preScale(-1f, 1f) // Flip horizontally
        val flippedBitmap =
            Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)

        // Convert back to ImageBitmap
        return flippedBitmap.asImageBitmap()
    }

    @Composable
    fun getWalkingMapsL(): Array<ImageBitmap> {
        val imageArray = getWalkingMapsR()
        val leftImageArray = arrayOfNulls<ImageBitmap>(6)
        imageArray.forEachIndexed { index, imageBitmap ->
            leftImageArray[index]=flipImageBitmap(imageBitmap)
        }
 
        return leftImageArray.requireNoNulls()
    }

    @Composable
    fun getUpMaps(): Array<ImageBitmap> {
        return arrayOf(
            ImageBitmap.imageResource(id = R.drawable.up3_f),
            ImageBitmap.imageResource(id = R.drawable.up2_f),
            ImageBitmap.imageResource(id = R.drawable.up1_f),
        )
    }

    @Composable
    fun getFallMaps(): Array<ImageBitmap> {
        return arrayOf(
            ImageBitmap.imageResource(id = R.drawable.fall_f),
            ImageBitmap.imageResource(id = R.drawable.fall2_f),
            ImageBitmap.imageResource(id = R.drawable.fall3_f),
        )
    }

    @Composable
    fun getJumpMaps(): Array<ImageBitmap> {
        return arrayOf(
            ImageBitmap.imageResource(id = R.drawable.air1_f),
            ImageBitmap.imageResource(id = R.drawable.air2_f),
        )
    }

    @Composable
    fun getIdleMaps(): Array<ImageBitmap> {
        return arrayOf(
            ImageBitmap.imageResource(id = R.drawable.idle_f)
        )
    }

    @Composable
    fun getClickedMaps(): Array<ImageBitmap> {
        return arrayOf(
            ImageBitmap.imageResource(id = R.drawable.ouch_f)
        )
    }

    lateinit var aniManager: AnimationManager

    @Composable
    fun BuildSprite() {
        aniManager = AnimationManager(
            arrayOf(
                Animation(getIdleMaps(), animTime = 1F, dialog = ImageBitmap.imageResource(id = R.drawable.dialog)),
                Animation(getWalkingMapsL(), animTime = 8F, dialog = ImageBitmap.imageResource(id = R.drawable.dialog)),
                Animation(getJumpMaps(), animTime = 2F, dialog = ImageBitmap.imageResource(id = R.drawable.dialog)),
                Animation(getFallMaps(), animTime = 3F, freezLastFrame = true, dialog = ImageBitmap.imageResource(id = R.drawable.dialog)),
                Animation(getWalkingMapsR(), animTime = 8F, dialog = ImageBitmap.imageResource(id = R.drawable.dialog)),
                Animation(getUpMaps(), animTime = 10F, dialog = ImageBitmap.imageResource(id = R.drawable.dialog)),
                Animation(getClickedMaps(), animTime = 1F, dialog = ImageBitmap.imageResource(id = R.drawable.dialog)),
            )
        )

    }

    @Composable
    fun returnToCenter(
        offsetX: Float,
        offsetY: Float,
        personViewModel: PersonInvitedViewModel,
    ) {
        var staticOffsetx = offsetX
        val scope = CoroutineScope(Dispatchers.Main)
        val personState by personViewModel.personState.collectAsState()
        if (staticOffsetx != 0F && offsetY == 0F) {
            if (staticOffsetx > 0) {
                aniManager.playAnim(1)
                LaunchedEffect(Unit) {
                    scope.launch {
                        while (staticOffsetx >= 10F) {
                            if (personState != PersonState.RET_TO_CENTER) return@launch
                            personViewModel.setOffsetX(staticOffsetx - 1F)
                            staticOffsetx -= 1F
                            delay(10)
                        }
                        personViewModel.setOffsetX(0F)
                        aniManager.playAnim(0)
                    }
                }
            } else {
                aniManager.playAnim(4)
                LaunchedEffect(Unit) {
                    scope.launch {
                        while (staticOffsetx <= 10F) {
                            if (personState != PersonState.RET_TO_CENTER) return@launch
                            personViewModel.setOffsetX(staticOffsetx + 1F)
                            staticOffsetx += 1F
                            delay(10)

                        }
                        personViewModel.setOffsetX(0F)
                        aniManager.playAnim(0)
                    }
                }
            }
        }
    }

    @Composable
    fun Draw(
        personViewModel: PersonInvitedViewModel = viewModel()
    ) {
        val userPreferences by personViewModel.userPreferences.collectAsState(initial = UserPreferences())
        val offsetX by personViewModel.offsetX.collectAsState()
        var offsetY by remember { mutableStateOf(0f) }
        val personState by personViewModel.personState.collectAsState()
        val interactionSource = remember { MutableInteractionSource() }
        val scope = CoroutineScope(Dispatchers.Main)
        when (personState) {
            PersonState.IDLE -> {
                aniManager.playAnim(0)
            }

            PersonState.BEING_DRAGED -> {
                aniManager.playAnim(2)
            }

            PersonState.FALLING -> {
                LaunchedEffect(Unit) {
                    scope.launch {
                        var reps = 0F
                        while (offsetY <= 10F) {

                            reps++
                            offsetY += 10F + reps
                            delay(10)
                        }
                        offsetY = 0F

                        aniManager.playAnim(3)

                        delay(2000)
                        if (personState == PersonState.FALLING) {
                            personViewModel.setState(PersonState.RISING)
                        }

                    }
                }
            }

            PersonState.RET_TO_CENTER -> {
                returnToCenter(offsetX = offsetX, offsetY = offsetY, personViewModel)
            }

            PersonState.CLICKED -> {
                LaunchedEffect(Unit) {
                    launch {
                        aniManager.playAnim(6)
                        delay(100)
                        personViewModel.setState(PersonState.IDLE)
                        personViewModel.hideDialog(userPreferences!!)
                    }
                }
            }

            PersonState.RISING -> {
                LaunchedEffect(Unit) {
                    Log.i("anim", "rise")
                    aniManager.playAnim(5)
                    delay(700)
                    personViewModel.setState(PersonState.RET_TO_CENTER)
                }
            }
        }
        aniManager.Draw(modifier = Modifier
            .offset {
                IntOffset(
                    offsetX.roundToInt(),
                    if (offsetY.roundToInt() <= 0) offsetY.roundToInt() else 0
                )
            }
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = {
                        Log.i("drag", "start")
                        personViewModel.setState(PersonState.BEING_DRAGED)
                    },
                    onDragEnd = {
                        Log.i("drag", "ended")
                        personViewModel.setState(PersonState.FALLING)
                        val scope = CoroutineScope(Dispatchers.Main)
                    },
                ) { change, dragAmount ->
                    change.consume()

                    personViewModel.setOffsetX(offsetX + dragAmount.x)
                    offsetY += dragAmount.y
                }
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null,
            ) {
                if (personState != PersonState.CLICKED) {
                    personViewModel.setState(PersonState.CLICKED)
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