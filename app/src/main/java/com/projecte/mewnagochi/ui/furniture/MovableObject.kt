package com.projecte.mewnagochi.ui.furniture

import android.content.Context

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import com.projecte.mewnagochi.screens.home.HomeScreenViewModel
import com.projecte.mewnagochi.screens.home.movableObject.MovableObjectState
import com.projecte.mewnagochi.ui.theme.PersonState
import kotlinx.coroutines.launch
import kotlin.math.roundToInt


open class MovableObject(
    val id: String,
    val res: Int,

    ) {


    lateinit var context: Context

    @Composable
    open fun getAppSetting(): MovableObjectState {
        return context.dataStore.data.collectAsState(
            initial = MovableObjectState()
        ).value
    }


    @Composable
    fun Draw(
        viewModel: HomeScreenViewModel = viewModel(),
    ) {
        context = LocalContext.current
        val appSettings = getAppSetting()

        var offsetX by remember { mutableFloatStateOf(appSettings.x) }
        var offsetY by remember { mutableFloatStateOf(appSettings.y) }
        var personState by remember { mutableStateOf(PersonState.IDLE) }
        val addedObject by viewModel.addedObject.collectAsState()
        val deletedObject by viewModel.deletedObject.collectAsState()
        val selectedId by viewModel.selectedFurnitureId.collectAsState()
        val isEditingFurniture by viewModel.isEditingFurniture.collectAsState()
        var visible by remember {
            mutableStateOf(false)
        }

        val scope = rememberCoroutineScope()
        if (addedObject == res) {
            LaunchedEffect(Unit) {
                show()
                visible = true
                viewModel.addObject(0)
            }
        }
        if (deletedObject == res) {

            LaunchedEffect(Unit) {
                hide()
                visible = false
                viewModel.deleteObject(0)
                viewModel.removeItem(res)
            }
        }
        if (selectedId != res) {
            personState = PersonState.IDLE
        }
        visible = appSettings.show
        offsetX = appSettings.x
        offsetY = appSettings.y
        val lifecycleOwner = LocalLifecycleOwner.current
        DisposableEffect(key1 = lifecycleOwner, effect = {
            val observer = LifecycleEventObserver { _, event ->
                if (event == Lifecycle.Event.ON_RESUME) {
                    offsetX = appSettings.x
                    offsetY = appSettings.y
                }
            }
            lifecycleOwner.lifecycle.addObserver(observer)
            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
            }
        })
        if (appSettings.show) {
            viewModel.addItem(res)
            Image(
                painter = painterResource(id = res),
                contentDescription = "",
                modifier = Modifier
                    .offset {
                        if (personState == PersonState.BEING_DRAGED) {
                            IntOffset(
                                offsetX.roundToInt(),
                                offsetY.roundToInt()
                            )
                        } else {
                            IntOffset(
                                appSettings.x.roundToInt(),
                                appSettings.y.roundToInt()
                            )
                        }
                    }
                    .then(
                        if (isEditingFurniture) {
                            Modifier
                                .pointerInput(Unit) {
                                    detectDragGestures(
                                        onDragStart = {
                                            if (personState == PersonState.CLICKED)
                                                personState = PersonState.BEING_DRAGED
                                        },
                                        onDragEnd = {
                                            viewModel.deselectFurniture()
                                            personState = PersonState.IDLE
                                            scope.launch {
                                                setX(offsetX, offsetY)
                                            }
                                        }
                                    ) { change, dragAmount ->
                                        if (personState == PersonState.BEING_DRAGED) {
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
                                    personState = if (personState != PersonState.CLICKED) {
                                        viewModel.selectFurniture(res)

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
                        } else Modifier
                    )
            )
        }
    }

    open suspend fun setX(x: Float, y: Float) {
        context.dataStore.updateData {
            it.copy(x = x, y = y)
        }
    }

    open suspend fun setY(y: Float) {

        context.dataStore.updateData {
            it.copy(y = y)
        }
    }

    open suspend fun show() {
        context.dataStore.updateData {
            it.copy(show = true)
        }
    }

    open suspend fun hide() {
        context.dataStore.updateData {
            it.copy(show = false)
        }
    }
}