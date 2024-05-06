package com.projecte.mewnagochi.ui.furniture

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import com.projecte.mewnagochi.screens.home.HomeScreenViewModel
import com.projecte.mewnagochi.services.storage.Item
import com.projecte.mewnagochi.ui.theme.PersonState
import kotlin.math.roundToInt

@Composable
fun MovableItem(
    viewModel: HomeScreenViewModel = viewModel(),
    item: Item,
) {
    var offsetX by remember { mutableFloatStateOf(item.posX) }
    var offsetY by remember { mutableFloatStateOf(item.posY) }
    var objectState by remember { mutableStateOf(PersonState.IDLE) }
    val screenUi by viewModel.uiState

    if (screenUi.selectedFurnitureId != item.id) {
        objectState = PersonState.IDLE
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(key1 = lifecycleOwner, effect = {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                offsetX = item.posX
                offsetY = item.posY
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    })
    if (item.visible) {
        Image(
            painter = painterResource(id = item.res),
            contentDescription = "",
            modifier = Modifier
                .offset {
                    //if (object_state == PersonState.BEING_DRAGED) {
                        IntOffset(
                            offsetX.roundToInt(),
                            offsetY.roundToInt()
                        )
                   // } else {
                     /*   IntOffset(
                            item.posX.roundToInt(),
                            item.posY.roundToInt()
                        )
                    }*/
                }
                .then(
                    if (screenUi.isEditingFurniture) {
                        Modifier
                            .pointerInput(Unit) {
                                detectDragGestures(
                                    onDragStart = {
                                        if (objectState == PersonState.CLICKED)
                                            objectState = PersonState.BEING_DRAGED
                                    },
                                    onDragEnd = {
                                        viewModel.deselectFurniture()
                                        objectState = PersonState.IDLE
                                        viewModel.updateItem(item.copy(posX = offsetX, posY = offsetY))
                                    }
                                ) { change, dragAmount ->
                                    if (objectState == PersonState.BEING_DRAGED) {
                                        change.consume()
                                        offsetX += dragAmount.x
                                        offsetY += dragAmount.y
                                    }
                                }
                            }
                            .clickable(
                                interactionSource = MutableInteractionSource(),
                                indication = null
                            ) {
                                objectState = if (objectState != PersonState.CLICKED) {
                                    viewModel.selectFurniture(item.id)

                                    PersonState.CLICKED
                                } else {
                                    viewModel.deselectFurniture()
                                    PersonState.IDLE
                                }
                            }
                            .then(
                                if (objectState == PersonState.CLICKED || objectState == PersonState.BEING_DRAGED) Modifier.border(
                                    2.dp,
                                    Color.Yellow
                                ) else Modifier
                            )
                    } else Modifier
                )
        )
    }
}



