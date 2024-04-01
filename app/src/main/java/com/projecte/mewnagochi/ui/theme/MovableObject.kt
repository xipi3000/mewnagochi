package com.projecte.mewnagochi.ui.theme

import android.graphics.Bitmap
import android.graphics.Matrix
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.projecte.mewnagochi.HomeScreenViewModel
import com.projecte.mewnagochi.PersonViewModel
import com.projecte.mewnagochi.R
import com.projecte.mewnagochi.ui.animation.Animation
import com.projecte.mewnagochi.ui.animation.AnimationManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt


class MovableObject (
    val id: String,
    val res: Int,
    private var x: Float = 0F,
    private var y : Float= 0F
) {




    @Composable
    fun Draw(viewModel: HomeScreenViewModel = viewModel(),

    ){
        var offsetX  by remember { mutableFloatStateOf(0F) }
        var offsetY by remember { mutableFloatStateOf(0F) }
        offsetX=x
        offsetY=y
        var personState  by remember { mutableStateOf(PersonState.IDLE) }
        val selectedId by viewModel.selectedFurnitureId.collectAsState()

        if(selectedId!=id){
            personState = PersonState.IDLE
        }
        Image(
            painter = painterResource(id = res),
            contentDescription = "",
            modifier = Modifier
                .offset {
                    IntOffset(
                        offsetX.roundToInt(),
                        offsetY.roundToInt()
                    )
                }
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = {
                            if (personState == PersonState.CLICKED)
                                personState = PersonState.BEING_DRAGED
                        },
                        onDragEnd = {
                            x = offsetX
                            y = offsetY
                        }
                    ) { change, dragAmount ->
                        if (personState == PersonState.BEING_DRAGED) {
                            change.consume()
                            offsetX += dragAmount.x
                            offsetY += dragAmount.y
                        }
                    }
                }
                .clickable {
                    personState = if (personState != PersonState.CLICKED) {
                        viewModel.selectFurniture(id)
                        Log.i("selected",id)
                        PersonState.CLICKED
                    } else {
                        viewModel.deselectFurniture()
                        PersonState.IDLE
                    }
                }
                .then(
                    if (personState == PersonState.CLICKED || personState == PersonState.BEING_DRAGED) Modifier.border(
                        2.dp,
                        Color.Yellow
                    ) else Modifier
                )
        )
    }
}